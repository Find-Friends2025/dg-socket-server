package com.dgsocketserver.service

import com.dgsocketserver.db.CustomMessageRepository
import com.dgsocketserver.db.MessageEntity
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class ChatMessageService(
    private val customMessageRepository: CustomMessageRepository
) {
    fun getMessages(chatRoomId: UUID, cursor: String, size: Int): List<MessageEntity> {
        return customMessageRepository.findMessagesByChatRoomIdWithCursor(
            chatRoomId = chatRoomId,
            cursor = ObjectId(cursor),
            size = size
        )
    }
}