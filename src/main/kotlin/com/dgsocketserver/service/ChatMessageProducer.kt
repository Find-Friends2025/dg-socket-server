package com.dgsocketserver.service

import com.dgsocketserver.exception.SessionExpiredException
import com.dgsocketserver.presentation.dto.ChatMessageDto
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bson.types.ObjectId
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class ChatMessageProducer(
    private val redisTemplate: RedisTemplate<String, Any>,
) {
    private val mapper = jacksonObjectMapper()

    fun publish(userId: String, roomId: UUID, message: ChatMessageDto) {
        val key = "chat:user:$userId"
        val entries = redisTemplate.opsForHash<String, String>().entries(key)
        if (entries.isEmpty()) throw SessionExpiredException()

        val record = StreamRecords.newRecord()
            .ofMap(
                mapOf(
                    "id" to ObjectId().toString(),
                    "roomId" to roomId.toString(),
                    "senderId" to userId,
                    "senderName" to entries["name"]!!,
                    "senderProfileImage" to entries["profileImg"],
                    "message" to message.message,
                    "images" to mapper.writeValueAsString(message.images), // 리스트를 JSON 문자열로 변환
                    "sendAt" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) // 날짜도 문자열로 변환
                )
            )
            .withStreamKey("chat:stream")

        redisTemplate.opsForStream<String, Any>().add(record)
    }
}
