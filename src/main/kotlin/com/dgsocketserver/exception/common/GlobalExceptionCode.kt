package com.dgsocketserver.exception.common


enum class GlobalExceptionCode(
    override val status: Int,
    override val message: String
) : ExceptionCode {
    METHOD_NOT_SUPPORTED(400, "잘못된 메서드"),
    ACCESS_DENIED(403, "접근이 거부되었습니다"),
    MEDIA_TYPE_NOT_SUPPORTED(400, "잘못된 미디어 타입"),
    MEDIA_TYPE_MISS_MATCHED(400, "잘못된 미디어 값"),
    PARAMETER_NOT_FOUND(400, "잘못된 파라미터"),
    PARAMETER_NOT_VALID(400, "잘못된 파라미터"),
    FAILED_AUTHORIZATION(403, "인증 에러"),
    SESSION_EXPIRED(440, "세션 만료"),
    INTERNAL_SERVER(500, "서버 오류"),;

    override val exceptionName: String
        get() = this.name
}
