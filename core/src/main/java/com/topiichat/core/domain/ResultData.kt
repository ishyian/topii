package com.topiichat.core.domain

import com.topiichat.core.exception.domain.ErrorDomain

sealed class ResultData<out T> {

    data class Success<T>(
        val data: T
    ) : ResultData<T>()

    data class Fail(
        val error: ErrorDomain
    ) : ResultData<Nothing>()

    fun <R> transformData(
        map: (T) -> R
    ): ResultData<R> = when (this) {
        is Success -> {
            Success(map.invoke(data))
        }
        is Fail -> {
            Fail(error)
        }
    }
}