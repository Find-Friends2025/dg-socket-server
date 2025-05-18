package com.dgsocketserver.presentation.handler

import com.dgsocketserver.db.MessageEntity
import com.dgsocketserver.service.ChatMessageService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping("/message")
class MessageController(
    private val chatMessageService: ChatMessageService
) {
    @GetMapping
    fun getMessages(
        @RequestParam chatRoomId: UUID,
        @RequestParam cursor: String,
        @RequestParam(defaultValue = "20") size: Int
    ): List<MessageEntity> {
        return chatMessageService.getMessages(chatRoomId, cursor, size)
    }
}