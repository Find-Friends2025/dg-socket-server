package com.dgsocketserver.service

import com.dgsocketserver.db.ChatStatusEnum
import com.dgsocketserver.db.MessageEntity
import com.dgsocketserver.db.MessageRepository
import jakarta.annotation.PreDestroy
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
import java.time.LocalDateTime
import java.util.*

@Service
class ChatMessageConsumer(
    private val simpMessagingTemplate: SimpMessagingTemplate,
    private val messageRepository: MessageRepository,
    private val redisTemplate: RedisTemplate<String, Any>,
    private val streamMessageListenerContainer: StreamMessageListenerContainer<String, MapRecord<String, String, String>>,
) {
    @EventListener(ApplicationReadyEvent::class)
    fun startListening() {
        val streamKey = "chat:stream"
        val groupName = "socket-group"

        streamMessageListenerContainer.receiveAutoAck(
            Consumer.from(groupName, "socket-consumer-1"),
            StreamOffset.create(streamKey, ReadOffset.lastConsumed())
        ) { message: MapRecord<String, String, String> ->
            println(message.value)
            // 메시지 처리 로직은 동일
            val value = message.value
            val roomId = value["roomId"]!!
            val senderId = value["senderId"]!!
            val document = MessageEntity(
                id = ObjectId(value["id"]!!),
                chatRoomId = UUID.fromString(roomId),
                senderId = senderId,
                senderName = value["senderName"]!!,
                senderProfileImage = value["senderProfileImage"],
                message = value["message"]!!,
                images = value["images"]?.split(","),
                sendAt = LocalDateTime.parse(value["sendAt"]!!),
                messageStatus = ChatStatusEnum.SENT
            )
            simpMessagingTemplate.convertAndSend("/topic/room.$roomId", messageRepository.save(document))
            val users: Set<String> = redisTemplate.opsForSet()
                .members("chat:info:$roomId:users")
                ?.map { it.toString() }
                ?.toSet() ?: emptySet()
            val receiverUserId = users.firstOrNull { it != senderId }
            if (receiverUserId != null && redisTemplate.hasKey("chat:user:$receiverUserId")) {
                // TODO: FCM 로직 추가
            }
        }

        streamMessageListenerContainer.start()
    }

    @PreDestroy
    fun onDestroy() {
        println("onDestroy 호출됨")
        streamMessageListenerContainer.stop()
        println("리스너 컨테이너 stop() 호출 완료")
    }
}
