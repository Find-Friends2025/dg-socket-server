package com.dgsocketserver.presentation.interceptor

import com.dgsocketserver.client.TokenVerifyDto
import com.dgsocketserver.client.TokenVerifyInternalApiClient
import com.dgsocketserver.exception.AccessDeniedException
import com.dgsocketserver.exception.SessionExpiredException
import com.dgsocketserver.support.properties.InternalApiProperties
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class AuthHandshakeInterceptor(
    private val redisTemplate: StringRedisTemplate,
    private val tokenVerifyInternalApiClient: TokenVerifyInternalApiClient,
    private val internalApiProperties: InternalApiProperties
) : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        val token = request.headers.getFirst("Authorization") ?: throw SessionExpiredException()
        println(token)
        println(internalApiProperties.apiKey)
        val userId = tokenVerifyInternalApiClient.verifyToken(
            token = TokenVerifyDto(token),
            internalApiKey = internalApiProperties.apiKey
        ) ?: throw AccessDeniedException()
        if (!redisTemplate.hasKey("chat:user:$userId")) throw AccessDeniedException()

        attributes["userId"] = userId
        return true
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) = Unit
}
