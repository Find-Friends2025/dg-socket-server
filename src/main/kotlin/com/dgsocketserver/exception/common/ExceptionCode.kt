package com.dgsocketserver.exception.common

interface ExceptionCode {
    val status: Int

    val exceptionName: String

    val message: String
}
