package com.dgsocketserver.exception.common

open class CustomException(
    val exceptionCode: ExceptionCode
) : RuntimeException()
