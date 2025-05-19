package com.dgsocketserver.db

import org.bson.types.ObjectId
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Component
import java.util.*

@Component
class CustomMessageRepositoryImpl(
    private val mongoTemplate: MongoTemplate
) : CustomMessageRepository {

    override fun findMessagesByChatRoomIdWithCursor(
        chatRoomId: UUID,
        cursor: ObjectId?,
        size: Int
    ): List<MessageEntity> {
        val criteria = Criteria.where("chatRoomId").`is`(chatRoomId)

        if (cursor != null) {
            criteria.and("_id").lt(cursor) // 커서 기준 이전 메시지만 조회
        }

        val query = Query(criteria)
            .limit(size)
            .with(Sort.by(Sort.Direction.DESC, "_id")) // 최신 순

        return mongoTemplate.find(query, MessageEntity::class.java)
    }
}
