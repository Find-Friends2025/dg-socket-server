package com.dgsocketserver.presentation.handler

import com.dgsocketserver.exception.SessionExpiredException
import com.dgsocketserver.presentation.dto.ChatMessageDto
import com.dgsocketserver.service.ChatMessageProducer
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.web.socket.WebSocketSession

@Controller
class ChatController(
    private val producer: ChatMessageProducer
) {

    @MessageMapping("/chat.message")
    fun receiveMessage(
        session: WebSocketSession,
        message: ChatMessageDto
    ) {
        try {
            val userId = session.attributes["userId"] as? String ?: throw SessionExpiredException()
            producer.publish(userId, message.roomId, message)
        } catch (e: Exception) {
            println(e.message)
        }
    }
}
