package com.dgsocketserver.presentation.interceptor

import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class SessionRegistry {
    private val sessionIdToUserId: MutableMap<String, String> = ConcurrentHashMap()

    fun put(sessionId: String, userId: String) {
        sessionIdToUserId[sessionId] = userId
    }

    fun getUserId(sessionId: String): String? = sessionIdToUserId[sessionId]

    fun remove(sessionId: String) {
        sessionIdToUserId.remove(sessionId)
    }
}
