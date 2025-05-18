package com.dgsocketserver.db

import org.bson.types.ObjectId
import java.util.*

interface CustomMessageRepository {
    fun findMessagesByChatRoomIdWithCursor(
        chatRoomId: UUID,
        cursor: ObjectId?,
        size: Int
    ): List<MessageEntity>
}
