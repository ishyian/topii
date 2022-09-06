package com.topiichat.app.core.exception.domain

data class ErrorDomain(
    val message: String,
    val code: Int?,
    val exceptionClass: Class<*>,
)

fun emitError(message: String) = ErrorDomain(message, 400, IllegalStateException::class.java)