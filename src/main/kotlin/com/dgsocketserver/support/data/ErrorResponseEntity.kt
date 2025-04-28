package com.dgsocketserver.support.data

import com.dgsocketserver.exception.common.ExceptionCode
import org.springframework.http.ResponseEntity

open class ErrorResponseEntity private constructor(status: Int, code: String, message: String) :
    Response(status, message) {
    companion object {
        fun responseEntity(e: ExceptionCode): ResponseEntity<ErrorResponseEntity> {
            return ResponseEntity
                .status(e.status)
                .body(
                    ErrorResponseEntity(
                        e.status,
                        e.exceptionName,
                        e.message
                    )
                )
        }

        fun of(e: ExceptionCode): ErrorResponseEntity {
            return ErrorResponseEntity(e.status, e.exceptionName, e.message)
        }

        fun of(status: Int, code: String, message: String): ErrorResponseEntity {
            return ErrorResponseEntity(status, code, message)
        }
    }
}
