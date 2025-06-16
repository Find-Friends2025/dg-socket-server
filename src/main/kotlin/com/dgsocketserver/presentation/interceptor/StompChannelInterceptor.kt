package com.dgsocketserver.presentation.interceptor

import com.dgsocketserver.client.TokenVerifyDto
import com.dgsocketserver.client.TokenVerifyInternalApiClient
import com.dgsocketserver.exception.AccessDeniedException
import com.dgsocketserver.exception.InvalidParameterException
import com.dgsocketserver.support.properties.InternalApiProperties
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Component
class StompChannelInterceptor(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val tokenVerifyInternalApiClient: TokenVerifyInternalApiClient,
    private val internalApiProperties: InternalApiProperties
) : ChannelInterceptor {

    private val sessionIdToUserId: MutableMap<String, String> = ConcurrentHashMap()

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        when (accessor.command) {
            StompCommand.CONNECT -> {
                val token = accessor.getFirstNativeHeader("Authorization")
                    ?.removePrefix("Bearer ")
                    ?.trim()
                    ?: throw AccessDeniedException()
                val userId = tokenVerifyInternalApiClient.verifyToken(
                    token = TokenVerifyDto(token),
                    internalApiKey = internalApiProperties.apiKey
                ) ?: throw AccessDeniedException()
                val sessionId = accessor.sessionId ?: throw AccessDeniedException()
                sessionIdToUserId[sessionId] = userId
            }

            StompCommand.SUBSCRIBE -> {
                val sessionId = accessor.sessionId ?: throw AccessDeniedException()
                val userId = sessionIdToUserId[sessionId] ?: throw AccessDeniedException()
                val destination = accessor.destination ?: throw InvalidParameterException()
                val roomId = destination.substringAfterLast("/")
                val roomUuid = try {
                    UUID.fromString(roomId)
                } catch (e: IllegalArgumentException) {
                    throw InvalidParameterException()
                }
                val isMember = redisTemplate.opsForSet().isMember("chat:info:$roomId:users", userId)
                if (isMember != true) throw AccessDeniedException()

                redisTemplate.opsForSet().add("chat:room:$roomUuid:connectedUsers", userId)
                println("sub success")
            }

            StompCommand.DISCONNECT -> {
                val sessionId = accessor.sessionId
                sessionId?.let { sessionIdToUserId.remove(it) }
            }

            else -> {}
        }

        return message
    }
}
