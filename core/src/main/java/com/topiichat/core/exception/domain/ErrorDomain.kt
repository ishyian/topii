package com.topiichat.core.exception.domain

import com.topiichat.core.exception.data.model.ServiceUnavailableException
import com.topiichat.core.exception.data.model.WrongRequestException

data class ErrorDomain(
    val message: String,
    val code: Int?,
    val exceptionClass: Class<*>,
)

fun emitError(message: String) = ErrorDomain(message, 400, WrongRequestException::class.java)
fun networkConnectionError() =
    ErrorDomain("Something wrong with your network connection", 400, ServiceUnavailableException::class.java)