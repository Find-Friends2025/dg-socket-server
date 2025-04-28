package com.dgsocketserver.presentation.handler

import com.dgsocketserver.exception.common.CustomException
import com.dgsocketserver.exception.common.GlobalExceptionCode
import com.dgsocketserver.support.data.ErrorResponseEntity
import com.dgsocketserver.support.logger.logger
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException
import org.springframework.messaging.handler.annotation.support.MethodArgumentTypeMismatchException
import org.springframework.validation.FieldError
import org.springframework.validation.ObjectError
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = logger()

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(e: CustomException): ResponseEntity<ErrorResponseEntity> {
        val code = e.exceptionCode
        log.error("Exception : {}, {}", code.status, code.message)
        return ErrorResponseEntity.responseEntity(code)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidException(e: MethodArgumentNotValidException): ResponseEntity<ErrorResponseEntity> {
        val message = getValidExceptionMessages(e.bindingResult?.allErrors ?: emptyList())
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorResponseEntity.of(
                    status = HttpStatus.BAD_REQUEST.value(),
                    code = GlobalExceptionCode.PARAMETER_NOT_VALID.name,
                    message = message
                )
            )
    }

    private fun getValidExceptionMessages(errors: List<ObjectError>): String {
        val message = StringBuilder()
        errors.forEach {
            val fieldError = it as FieldError
            message.append("${fieldError.field} ${it.defaultMessage}, ")
        }
        return message.removeSuffix(", ").toString()
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingServletRequestParameterException(): ResponseEntity<ErrorResponseEntity> {
        val code = GlobalExceptionCode.PARAMETER_NOT_FOUND
        return ResponseEntity
            .status(400)
            .body(ErrorResponseEntity.of(code.status, code.name, code.message))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(): ResponseEntity<ErrorResponseEntity> {
        val code = GlobalExceptionCode.PARAMETER_NOT_FOUND
        return ResponseEntity
            .status(400)
            .body(ErrorResponseEntity.of(code.status, code.name, code.message))
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(): ResponseEntity<ErrorResponseEntity> {
        val code = GlobalExceptionCode.METHOD_NOT_SUPPORTED
        return ResponseEntity
            .status(400)
            .body(ErrorResponseEntity.of(code.status, code.name, code.message))
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(): ResponseEntity<ErrorResponseEntity> {
        val code = GlobalExceptionCode.MEDIA_TYPE_NOT_SUPPORTED
        return ResponseEntity
            .status(400)
            .body(ErrorResponseEntity.of(code.status, code.name, code.message))
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleMethodArgumentTypeMismatchException(): ResponseEntity<ErrorResponseEntity> {
        val code = GlobalExceptionCode.MEDIA_TYPE_MISS_MATCHED
        return ResponseEntity
            .status(400)
            .body(ErrorResponseEntity.of(code.status, code.name, code.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception, request: HttpServletRequest): ResponseEntity<ErrorResponseEntity> {
        val code = GlobalExceptionCode.INTERNAL_SERVER
        return ResponseEntity
            .status(500)
            .body(ErrorResponseEntity.of(code.status, code.name, code.message))
    }
}
