package com.dgsocketserver.service

import com.dgsocketserver.exception.SessionExpiredException
import com.dgsocketserver.presentation.dto.ChatMessageDto
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatMessageProducer(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    fun publish(userId: String, roomId: UUID, message: ChatMessageDto) {
        val key = "chat:user:$userId"
        val entries = redisTemplate.opsForHash<String, String>().entries(key)
        if (entries.isEmpty()) throw SessionExpiredException()

        val record = StreamRecords.newRecord()
            .ofMap(mapOf(
                "roomId" to roomId.toString(),
                "senderId" to userId,
                "senderName" to entries["name"]!!,
                "senderProfileImage" to entries["profileImg"],
                "message" to message.message,
                "images" to message.images
            ))
            .withStreamKey("chat:stream")
        
        redisTemplate.opsForStream<String, Any>().add(record)
    }
}
