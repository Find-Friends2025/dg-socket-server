package com.dgsocketserver.support.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "api.internal")
class InternalApiProperties {
    lateinit var baseUrl: String
    lateinit var apiKey: String
}