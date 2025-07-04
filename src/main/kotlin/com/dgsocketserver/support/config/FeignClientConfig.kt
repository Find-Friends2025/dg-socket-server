package com.dgsocketserver.support.config

import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Configuration

@Configuration
@EnableFeignClients(basePackages = ["com.dgsocketserver"])
class FeignClientConfig {
}