package com.dgsocketserver.support.config

import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import java.time.Duration

@Configuration
class RedisConfig(
    private val redisProperties: RedisProperties
) {

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(redisProperties.host, redisProperties.port)
    }

    @Bean
    fun stringRedisTemplate(redisConnectionFactory: RedisConnectionFactory): StringRedisTemplate {
        return StringRedisTemplate(redisConnectionFactory)
    }

    @Bean
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.setConnectionFactory(redisConnectionFactory)

        val stringSerializer = StringRedisSerializer()
        redisTemplate.keySerializer = stringSerializer
        redisTemplate.valueSerializer = stringSerializer
        redisTemplate.hashKeySerializer = stringSerializer
        redisTemplate.hashValueSerializer = stringSerializer

        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }


    @Bean
    fun streamMessageListenerContainer(redisConnectionFactory: RedisConnectionFactory): StreamMessageListenerContainer<String, MapRecord<String, String, String>>? {
        val options = StreamMessageListenerContainer.StreamMessageListenerContainerOptions
            .builder()
            .pollTimeout(Duration.ofMillis(100)) // 메시지를 소비할 주기 설정
            .build()

        val container = StreamMessageListenerContainer.create(redisConnectionFactory, options)

        return container
    }
}