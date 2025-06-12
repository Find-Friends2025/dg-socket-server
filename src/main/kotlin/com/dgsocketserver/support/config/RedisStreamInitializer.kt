package com.dgsocketserver.support.config

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.core.StreamOperations
import org.springframework.data.redis.core.StringRedisTemplate
import java.util.*

@Configuration
class RedisStreamInitializer(
    private val redisConnectionFactory: RedisConnectionFactory,
    private val redisTemplate: StringRedisTemplate
) {

    @PostConstruct
    fun init() {
        val streamKey = "chat:stream"
        val groupName = "socket-group"

        val streamOps: StreamOperations<String, String, String> = redisTemplate.opsForStream()

        // 스트림이 존재하지 않으면 dummy 메시지 삽입
        if (!redisTemplate.hasKey(streamKey)) {
            streamOps.add(streamKey, mapOf("init" to UUID.randomUUID().toString()))
        }

        // 그룹이 없으면 생성
        try {
            redisConnectionFactory.connection.streamCommands()
                .xGroupCreate(
                    streamKey.toByteArray(),
                    groupName.toByteArray().toString(),
                    ReadOffset.latest(),
                    true // MKSTREAM: 스트림이 없으면 생성
                )
            println("✅ Redis Consumer Group '$groupName' 생성 완료")
        } catch (e: Exception) {
            if (e.message?.contains("BUSYGROUP") == true) {
                println("ℹ️ Consumer Group '$groupName' 이미 존재함")
            } else {
                println("❌ Redis Consumer Group 생성 실패: ${e.message}")
                throw e
            }
        }
    }
}
