package com.dgsocketserver.service

import com.dgsocketserver.db.MessageEntity
import com.dgsocketserver.db.MessageRepository
import org.bson.types.ObjectId
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*

@Service
class ChatMessageConsumer(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val messageRepository: MessageRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
) {

    @EventListener(ApplicationReadyEvent::class)
    fun startListening() {
        val streamKey = "chat:stream"
        val groupName = "socket-group"

        val ops = redisTemplate.connectionFactory!!.connection
        try {
            ops.streamCommands().xGroupCreate(
                streamKey.toByteArray(),
                groupName,
                ReadOffset.latest(),
                true
            )
        } catch (e: Exception) {
            if (!e.message.orEmpty().contains("BUSYGROUP")) {
                throw e
            }
        }

        val options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
            .builder()
            .pollTimeout(Duration.ofSeconds(1))
            .build()

        val container = StreamMessageListenerContainer.create(redisTemplate.connectionFactory!!, options)

        container.receiveAutoAck(
            Consumer.from(groupName, "socket-consumer-1"),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed())
        ) { message: MapRecord<String, String, String> ->
            val value = message.value
            val roomId: String = value["roomId"]!!
            val senderId: String = value["senderId"]!!
            val document = MessageEntity(
                id = ObjectId(value["id"]!!),
                chatRoomId = UUID.fromString(roomId),
                senderId = senderId,
                senderName = value["senderName"]!!,
                senderProfileImage = value["senderProfileImage"],
                message = value["message"]!!,
                images = value["images"]?.split(","),
                sendAt = LocalDateTime.parse(value["sendAt"]!!),

            )
            simpMessagingTemplate.convertAndSend("/topic/room.$roomId", messageRepository.save(document))
            val users: Set<String> = redisTemplate.opsForSet()
                .members("chat:info:$roomId:users")
                ?.map { it.toString() }
                ?.toSet() ?: emptySet()
            val receiverUserId = users.first { it != senderId }
            if (redisTemplate.hasKey("chat:user:$receiverUserId")) {
                //TODO FCM 발송 로직 추가 + FCM TOKEN 저장 위치 결정
            }
        }
        container.start()
    }
}
