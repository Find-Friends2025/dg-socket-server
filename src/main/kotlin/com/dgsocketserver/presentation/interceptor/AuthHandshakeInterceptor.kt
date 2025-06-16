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
//    private val redisTemplate: StringRedisTemplate,
    private val tokenVerifyInternalApiClient: TokenVerifyInternalApiClient,
    private val internalApiProperties: InternalApiProperties
) : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        println("join auth interceptor")
        println("header: ${request.headers}")
        val token = request.headers.getFirst("Authorization") ?: throw SessionExpiredException()
        val rawToken = token.removePrefix("Bearer ").trim()
        val userId = tokenVerifyInternalApiClient.verifyToken(
            token = TokenVerifyDto(rawToken),
            internalApiKey = internalApiProperties.apiKey
        ) ?: throw AccessDeniedException()
//        if (!redisTemplate.hasKey("chat:user:$userId")) throw AccessDeniedException()
        println("user:")
        println(userId)
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
