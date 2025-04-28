package com.dgsocketserver.support.logger

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

fun <T : Any> T.logger(): Logger = LoggerFactory.getLogger(this::class.java)
