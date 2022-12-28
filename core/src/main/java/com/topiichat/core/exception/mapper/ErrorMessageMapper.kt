package com.topiichat.core.exception.mapper

import com.topiichat.core.domain.Mapper
import com.topiichat.core.exception.data.ErrorMessageDto

class ErrorMessageMapper : Mapper<ErrorMessageDto, String> {
    override fun map(input: ErrorMessageDto?): String {
        return input?.detail?.error ?: ""
    }
}