package com.dgsocketserver.db

import org.springframework.data.mongodb.repository.MongoRepository

interface MessageRepository : MongoRepository<MessageEntity, String> {
}