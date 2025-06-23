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
    fun getMessages(chatRoomId: UUID, cursor: String?, size: Int): List<MessageResponse> {
        val objectIdCursor = if (!cursor.isNullOrBlank() && ObjectId.isValid(cursor)) ObjectId(cursor) else null

        val messages = customMessageRepository.findMessagesByChatRoomIdWithCursor(
            chatRoomId = chatRoomId,
            cursor = objectIdCursor,
            size = size
        )
        return messages.map { message ->
            with(message) {
                MessageResponse(
                    id = id!!.toHexString(),
                    chatRoomId = chatRoomId,
                    senderId = senderId,
                    senderName = senderName,
                    senderProfileImage = senderProfileImage,
                    message = message.message,
                    images = images,
                    sendAt = sendAt,
                    messageStatus = messageStatus
                )
            }
        }
    }
}