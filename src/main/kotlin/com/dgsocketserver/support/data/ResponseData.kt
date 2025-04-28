package com.dgsocketserver.support.data

import org.springframework.http.HttpStatus

class ResponseData<T> private constructor(status: HttpStatus, message: String, val data: T) :
    Response(status.value(), message) {

    companion object {
        fun <T> of(status: HttpStatus, message: String, data: T): ResponseData<T> {
            return ResponseData(status, message, data)
        }

        fun <T> ok(message: String, data: T): ResponseData<T> {
            return ResponseData(HttpStatus.OK, message, data)
        }

        fun <T> created(message: String, data: T): ResponseData<T> {
            return ResponseData(HttpStatus.CREATED, message, data)
        }
    }
}

