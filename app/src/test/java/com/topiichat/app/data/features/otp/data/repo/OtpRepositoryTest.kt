package com.topiichat.app.data.features.otp.data.repo

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.data.core.MainDispatcherRule
import com.topiichat.app.data.core.TestAppDispatchers
import com.topiichat.app.data.features.otp.utils.OtpCodeTestUtils
import com.topiichat.app.features.otp.data.datasource.OtpCodeRemoteDataSource
import com.topiichat.app.features.otp.data.mapper.ResendOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.mapper.ValidOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.repo.OtpCodeRepositoryImpl
import com.topiichat.core.exception.data.ErrorParser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.HttpException
import retrofit2.Response

@RunWith(JUnit4::class)
class OtpRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestAppDispatchers()
    private val apiService = mock<ApiService>()

    private val validOtpCodeMapper = ValidOtpCodeRemoteMapper()
    private val resendOtpCodeMapper = ResendOtpCodeRemoteMapper()

    private val errorParser = ErrorParser()

    @ExperimentalCoroutinesApi
    private val repository = OtpCodeRepositoryImpl(
        OtpCodeRemoteDataSource(apiService),
        validOtpCodeMapper,
        resendOtpCodeMapper,
        testDispatcher
    )

    @ExperimentalCoroutinesApi
    @Test
    fun valid_otp_code_success() = runTest {
        whenever(apiService.validateOtpCode(any())).thenReturn(OtpCodeTestUtils.validOtpDto)
        repository.validateOtpCode("", "").transformData {
            assertEquals(it?.message, OtpCodeTestUtils.validOtpDto.message)
            assertEquals(
                it?.isSuccessful, OtpCodeTestUtils.validOtpDto.success
            )
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun valid_otp_code_fail() = runTest {
        val errorBody = "{\"detail\": {\"error\": \"Invalid otp code\"}}"
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.validateOtpCode(any()))
            .thenThrow(HttpException(Response.error<Any>(400, errorBody)))

        kotlin.runCatching {
            repository.validateOtpCode("", "")
        }.onFailure {
            assertEquals(errorParser.parse(it).message, "Invalid otp code")
        }
    }
}