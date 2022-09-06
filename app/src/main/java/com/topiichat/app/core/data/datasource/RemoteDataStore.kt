package com.topiichat.app.core.data.datasource

import com.topiichat.app.core.domain.ResultData

interface RemoteDataStore {
    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ResultData<T?>
}