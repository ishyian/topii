package com.topiichat.app.core.exception.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.core.exception.data.ErrorMessageDto

class ErrorMessageMapper : Mapper<ErrorMessageDto, String> {
    override fun map(input: ErrorMessageDto?): String {
        return input?.detail?.error ?: ""
    }
}