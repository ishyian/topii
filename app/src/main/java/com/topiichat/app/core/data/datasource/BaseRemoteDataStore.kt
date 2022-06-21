package com.topiichat.app.core.data.datasource

import com.topiichat.app.core.data.Dto
import com.topiichat.app.core.domain.RemoteFailStatus
import com.topiichat.app.core.domain.ResultData
import retrofit2.Response

abstract class BaseRemoteDataStore : RemoteDataStore {

    companion object {
        private const val ERROR_CODE_UNAUTHORIZED = 401
    }

    override suspend fun <T : Dto?> fetchResult(
        call: suspend () -> Response<T?>
    ): ResultData<T?> {
        return try {
            val response = call.invoke()
            if (response.isSuccessful) {
                ResultData.Success(response.body())
            } else {
                if (response.code() == ERROR_CODE_UNAUTHORIZED) {
                    ResultData.Fail(status = RemoteFailStatus.NoAuth())
                } else {
                    ResultData.Fail(status = RemoteFailStatus.Generic())
                }
            }
        } catch (e: RuntimeException) {
            ResultData.Fail(status = RemoteFailStatus.ServerUnavailable())
        }
    }
}