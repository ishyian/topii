package com.topiichat.app.core.domain

import com.topiichat.app.core.exception.domain.ErrorDomain

sealed class ResultData<out T> {

    data class Success<T>(
        val data: T? = null
    ) : ResultData<T>()

    data class Fail(
        val error: ErrorDomain
    ) : ResultData<Nothing>()

    object NetworkError : ResultData<Nothing>()

    fun <R> transformData(
        map: (T?) -> R
    ): ResultData<R> = when (this) {
        is Success -> {
            Success(map.invoke(data))
        }
        is Fail -> {
            Fail(error)
        }
        is NetworkError -> {
            NetworkError
        }
    }
}