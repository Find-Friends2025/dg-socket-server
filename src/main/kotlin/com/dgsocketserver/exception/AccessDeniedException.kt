package com.dgsocketserver.exception

import com.dgsocketserver.exception.common.CustomException
import com.dgsocketserver.exception.common.GlobalExceptionCode

class AccessDeniedException : CustomException(GlobalExceptionCode.ACCESS_DENIED)
