package com.dgsocketserver.exception

import com.dgsocketserver.exception.common.CustomException
import com.dgsocketserver.exception.common.GlobalExceptionCode

class SessionExpiredException : CustomException(GlobalExceptionCode.SESSION_EXPIRED)