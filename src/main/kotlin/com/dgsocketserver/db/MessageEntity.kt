package com.dgsocketserver.db

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import java.util.UUID

@Document(collection = "messages")
class MessageEntity(
    @Id
    val id: ObjectId? = null,
    val chatRoomId: UUID,
    val senderId: String,
    val senderName: String,
    val senderProfileImage: String?,
    val message: String,
    val images: List<String>?,
    val sendAt: LocalDateTime = LocalDateTime.now(),
    var messageStatus: ChatStatusEnum = ChatStatusEnum.SENT
) {
}