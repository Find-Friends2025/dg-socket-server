package com.dgsocketserver.db

import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime
import java.util.*

interface MessageRepository : MongoRepository<MessageEntity, String> {

}