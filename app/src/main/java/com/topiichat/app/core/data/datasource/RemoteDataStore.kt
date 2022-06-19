package com.topiichat.app.core.data.datasource

import com.topiichat.app.core.data.Dto
import com.topiichat.app.core.domain.ResultData
import retrofit2.Response

interface RemoteDataStore {
    suspend fun <T : Dto?> fetchResult(
        call: suspend () -> Response<T?>
    ): ResultData<T?>
}