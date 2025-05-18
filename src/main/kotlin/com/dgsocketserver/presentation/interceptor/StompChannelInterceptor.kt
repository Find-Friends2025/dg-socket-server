package com.dgsocketserver.presentation.interceptor

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap


@Component
class StompChannelInterceptor(
    private val redisTemplate: RedisTemplate<String, Any>,
) : ChannelInterceptor {
    private val sessionIdToUserId: MutableMap<String?, String> = ConcurrentHashMap()

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = StompHeaderAccessor.wrap(message)

        if (StompCommand.CONNECT == accessor.command) {
            val sessionId = accessor.sessionId
            val user = accessor.user
            if (user != null) {
                sessionIdToUserId[sessionId] = user.name
            }
        } else if (StompCommand.DISCONNECT == accessor.command) {
            val sessionId = accessor.sessionId
            val userId = sessionIdToUserId.remove(sessionId)
            if (userId != null) {
                redisTemplate.delete("chat:user:$userId")
            }
        }
        return message
    }
}