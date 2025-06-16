package com.dgsocketserver.service

import com.dgsocketserver.db.ChatStatusEnum
import java.time.LocalDateTime
import java.util.*

data class MessageResponse(
    val id: String,
    val chatRoomId: UUID,
    val senderId: String,
    val senderName: String,
    val senderProfileImage: String?,
    val message: String,
    val images: List<String>?,
    val sendAt: LocalDateTime,
    val messageStatus: ChatStatusEnum
)