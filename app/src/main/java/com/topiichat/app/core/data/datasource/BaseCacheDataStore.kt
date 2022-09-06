package com.topiichat.app.core.data.datasource

import com.topiichat.app.core.data.Dto
import com.topiichat.app.core.domain.CacheFailStatus
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.exception.domain.ErrorDomain

abstract class BaseCacheDataStore : CacheDataStore {

    override suspend fun <T : Dto?> getResult(
        cacheFailStatus: CacheFailStatus,
        call: suspend () -> T?
    ): ResultData<T?> {
        return try {
            val response = call.invoke()
            ResultData.Success(response)
        } catch (e: RuntimeException) {
            ResultData.Fail(
                error = ErrorDomain(
                    message = e.message ?: "Read local data error",
                    code = null,
                    exceptionClass = e.javaClass
                )
            )
        }
    }
}