package com.dgsocketserver.support.config

import com.dgsocketserver.presentation.interceptor.AuthHandshakeInterceptor
import com.dgsocketserver.presentation.interceptor.StompChannelInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val authHandshakeInterceptor: AuthHandshakeInterceptor,
    private val stompChannelInterceptor: StompChannelInterceptor,
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(registry: MessageBrokerRegistry) {
        registry.enableSimpleBroker("/topic")
        registry.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            .addInterceptors(authHandshakeInterceptor)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompChannelInterceptor)
    }
}
