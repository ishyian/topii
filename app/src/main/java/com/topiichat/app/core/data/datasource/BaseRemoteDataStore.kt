package com.topiichat.app.core.data.datasource

import com.topiichat.core.domain.ResultData
import com.topiichat.core.exception.data.ErrorParser
import com.topiichat.core.exception.domain.networkConnectionError
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

abstract class BaseRemoteDataStore : RemoteDataStore {

    private val errorParser = ErrorParser()

    override suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ResultData<T> {
        return try {
            ResultData.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultData.Fail(networkConnectionError())
                is HttpException -> {
                    val errorResponse = errorParser.parse(throwable)
                    ResultData.Fail(error = errorResponse)
                }
                else -> {
                    Timber.d("error ${throwable.localizedMessage}")
                    Timber.e(throwable)
                    ResultData.Fail(error = errorParser.defaultError)
                }
            }
        }
    }
}