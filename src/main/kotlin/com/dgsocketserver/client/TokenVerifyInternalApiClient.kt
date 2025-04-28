package com.dgsocketserver.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "TokenVerify", url = "\${api.internal.base-url}")
interface TokenVerifyInternalApiClient {
    @PostMapping("/token/verify")
    fun verifyToken(
        @RequestBody token: String,
        @RequestHeader("X-Internal-Api-Key") internalApiKey: String
    ): String?
}