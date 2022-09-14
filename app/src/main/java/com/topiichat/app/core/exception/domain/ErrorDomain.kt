package com.topiichat.app.core.exception.domain

import java.io.IOException

data class ErrorDomain(
    val message: String,
    val code: Int?,
    val exceptionClass: Class<*>,
)

fun emitError(message: String) = ErrorDomain(message, 400, IllegalStateException::class.java)
fun networkConnectionError() = ErrorDomain("Something wrong with your network connection", 400, IOException::class.java)