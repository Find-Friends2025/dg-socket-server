package com.dgsocketserver.presentation.handler

import com.dgsocketserver.exception.common.GlobalExceptionCode
import com.dgsocketserver.support.data.ErrorResponseEntity
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler
import java.nio.charset.StandardCharsets

@Configuration
class StompErrorHandler(
    private val objectMapper: ObjectMapper,
    private val redisTemplate: StringRedisTemplate
) : StompSubProtocolErrorHandler() {

    override fun handleClientMessageProcessingError(clientMessage: Message<ByteArray>?, ex: Throwable): Message<ByteArray>? {

        if (clientMessage != null) {
            val accessor = StompHeaderAccessor.wrap(clientMessage)
            val userId = accessor.user?.name?.toLongOrNull()
            val roomId = accessor.getFirstNativeHeader("roomId")

            if (userId != null && roomId != null) {
                val redisKey = "chat:user:$userId:room:$roomId"
                val authState = redisTemplate.opsForValue().get(redisKey)

                if (authState != "authenticated") {
                    return sendErrorMessage(
                        ErrorResponseEntity.of(GlobalExceptionCode.FAILED_AUTHORIZATION)
                    )
                }
            }
        }

        return sendErrorMessage(ErrorResponseEntity.of(GlobalExceptionCode.INTERNAL_SERVER))
    }

    private fun sendErrorMessage(errorResponse: ErrorResponseEntity): Message<ByteArray> {
        val headers = StompHeaderAccessor.create(StompCommand.ERROR).apply {
            message = errorResponse.message
        }

        val payload = try {
            objectMapper.writeValueAsString(errorResponse).toByteArray(StandardCharsets.UTF_8)
        } catch (e: Exception) {
            errorResponse.message.toByteArray(StandardCharsets.UTF_8)
        }

        return MessageBuilder.createMessage(payload, headers.messageHeaders)
    }
}
