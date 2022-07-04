package com.topiichat.app.core.data.datasource

import com.topiichat.app.core.data.Dto
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.exception.data.ErrorParser
import retrofit2.HttpException
import java.io.IOException

abstract class BaseRemoteDataStore : RemoteDataStore {

    private val errorParser = ErrorParser()

    override suspend fun <T : Dto?> safeApiCall(
        apiCall: suspend () -> T?
    ): ResultData<T?> {
        return try {
            ResultData.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultData.NetworkError
                is HttpException -> {
                    val errorResponse = errorParser.parse(throwable)
                    ResultData.Fail(error = errorResponse)
                }
                else -> {
                    ResultData.Fail(error = errorParser.defaultError)
                }
            }
        }
    }
}