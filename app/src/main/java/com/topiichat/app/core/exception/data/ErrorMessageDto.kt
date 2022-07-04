package com.topiichat.app.core.exception.data

import com.squareup.moshi.Json

data class ErrorMessageDto(
    @Json(name = "detail")
    val detail: ErrorDetailDto?
) {
    data class ErrorDetailDto(
        @Json(name = "error")
        val error: String
    )
}
