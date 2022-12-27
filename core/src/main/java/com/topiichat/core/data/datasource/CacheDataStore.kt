package com.topiichat.core.data.datasource

import com.topiichat.core.data.Dto
import com.topiichat.core.domain.CacheFailStatus
import com.topiichat.core.domain.ResultData

interface CacheDataStore {
    suspend fun <T : Dto?> getResult(
        cacheFailStatus: CacheFailStatus = CacheFailStatus.Read(),
        call: suspend () -> T?
    ): ResultData<T?>
}