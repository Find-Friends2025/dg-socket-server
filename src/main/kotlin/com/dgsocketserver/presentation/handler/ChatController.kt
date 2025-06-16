package com.dgsocketserver.presentation.handler

import com.dgsocketserver.exception.SessionExpiredException
import com.dgsocketserver.presentation.dto.ChatMessageDto
import com.dgsocketserver.presentation.interceptor.SessionRegistry
import com.dgsocketserver.service.ChatMessageProducer
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.messaging.simp.SimpMessageHeaderAccessor
import org.springframework.stereotype.Controller
import org.springframework.web.socket.WebSocketSession

@Controller
class ChatController(
    private val producer: ChatMessageProducer,
    private val sessionRegistry: SessionRegistry
) {

    @MessageMapping("/chat.message")
    fun receiveMessage(
        @Payload message: ChatMessageDto,
        headerAccessor: SimpMessageHeaderAccessor
    ) {
        try {
            val sessionId = headerAccessor.sessionId ?: throw SessionExpiredException()
            val userId = sessionRegistry.getUserId(sessionId) ?: throw SessionExpiredException()
            producer.publish(userId, message.roomId, message)
        } catch (e: Exception) {
            println(e.message)
        }
    }

}
