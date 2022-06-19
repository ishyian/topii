package com.topiichat.app.core.domain

sealed class ResultData<T> {

    abstract fun <R> transform(map: (T?) -> R): R
    abstract fun transform(): ResultDataStatus

    data class Success<T>(
        val data: T? = null,
        val status: ResultDataStatus = ResultDataStatus.Ok
    ) : ResultData<T>() {

        override fun <R> transform(map: (T?) -> R): R {
            return map.invoke(data)
        }

        override fun transform(): ResultDataStatus {
            return status
        }
    }

    data class Fail<T>(
        val data: T? = null,
        val status: ResultDataStatus
    ) : ResultData<T>() {

        override fun <R> transform(map: (T?) -> R): R {
            return map.invoke(data)
        }

        override fun transform(): ResultDataStatus {
            return status
        }
    }

    fun <R> transformData(
        map: (T?) -> R
    ): ResultData<R> = when(this) {
        is Success -> {
            Success(this.transform(map))
        }
        is Fail -> {
            Fail(this.transform(map), this.transform())
        }
    }

}