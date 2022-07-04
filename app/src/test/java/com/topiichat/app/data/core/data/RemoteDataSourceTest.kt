package com.topiichat.app.data.core.data

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.exception.data.ErrorParser
import com.topiichat.app.core.exception.domain.ErrorDomain
import com.topiichat.app.data.core.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

@RunWith(JUnit4::class)
class RemoteDataSourceTest {
    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    private val testRemoteDataSource = TestRemoteDataSource()
    private val errorParser = ErrorParser()

    @ExperimentalCoroutinesApi
    @Test
    fun api_call_success() = runTest {
        val lambdaResult = TestDto()
        val result = testRemoteDataSource.safeApiCall { lambdaResult }
        assertEquals(ResultData.Success(lambdaResult), result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun api_call_network_error() = runTest {
        val result = testRemoteDataSource.safeApiCall { throw IOException() }
        assertEquals(ResultData.NetworkError, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun api_call_http_error() = runTest {
        val errorBody = "{\"detail\": {\"error\": \"Invalid parameter\"}}"
            .toResponseBody("application/json".toMediaTypeOrNull())
        val result = testRemoteDataSource.safeApiCall { throw HttpException(Response.error<Any>(422, errorBody)) }
        val expectedResult = ResultData.Fail(ErrorDomain("Invalid parameter", 422, HttpException::class.java))
        assertEquals(expectedResult, result)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun api_call_default_error() = runTest {
        val result = testRemoteDataSource.safeApiCall { throw IllegalStateException() }
        assertEquals(ResultData.Fail(errorParser.defaultError), result)
    }
}