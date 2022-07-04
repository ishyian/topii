package com.topiichat.app.core.data.datasource

import com.topiichat.app.core.data.Dto
import com.topiichat.app.core.domain.ResultData

interface RemoteDataStore {
    suspend fun <T : Dto?> safeApiCall(
        apiCall: suspend () -> T?
    ): ResultData<T?>
}