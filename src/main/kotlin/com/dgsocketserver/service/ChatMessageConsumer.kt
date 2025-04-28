package com.dgsocketserver.service

import com.dgsocketserver.db.MessageEntity
import com.dgsocketserver.db.MessageRepository
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
        val options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
            .builder()
            .pollTimeout(Duration.ofSeconds(1))
            .build()

        val container = StreamMessageListenerContainer.create(redisTemplate.connectionFactory!!, options)

        container.receiveAutoAck(
            Consumer.from("chat-group", "chat-consumer-1"),
            StreamOffset.create("chat:stream", ReadOffset.lastConsumed())
        ) { message: MapRecord<String, String, String> ->
            val value = message.value
            val roomId: String = value["roomId"]!!
            val document = MessageEntity(
                chatRoomId = UUID.fromString(roomId),
                senderId = value["senderId"]!!,
                senderName = value["senderName"]!!,
                senderProfileImage = value["senderProfileImage"],
                message = value["message"]!!,
                images = value["images"]?.split(","),
                sendAt = LocalDateTime.now()
            )
            simpMessagingTemplate.convertAndSend("/topic/room.$roomId", messageRepository.save(document))
        }
        container.start()
    }
}
