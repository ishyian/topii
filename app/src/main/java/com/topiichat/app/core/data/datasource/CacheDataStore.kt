package com.topiichat.app.core.data.datasource

import com.topiichat.app.core.data.Dto
import com.topiichat.app.core.domain.CacheFailStatus
import com.topiichat.app.core.domain.ResultData

interface CacheDataStore {
    suspend fun <T : Dto?> getResult(
        cacheFailStatus: CacheFailStatus = CacheFailStatus.Read(),
        call: suspend () -> T?
    ): ResultData<T?>
}