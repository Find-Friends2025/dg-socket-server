package com.dgsocketserver.presentation.dto

import java.util.UUID

data class ChatMessageDto(
    val roomId: UUID,
    val message: String,
    val images: List<String>
)