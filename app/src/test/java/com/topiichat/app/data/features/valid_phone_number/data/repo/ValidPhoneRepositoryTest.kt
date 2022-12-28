package com.topiichat.app.data.features.valid_phone_number.data.repo

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.data.core.MainDispatcherRule
import com.topiichat.app.data.core.TestAppDispatchers
import com.topiichat.app.data.features.valid_phone_number.utils.ValidPhoneNumberTestUtils
import com.topiichat.app.features.valid_phone_number.data.datasource.ValidPhoneRemoteDataStore
import com.topiichat.app.features.valid_phone_number.data.mapper.VerifyPhoneRemoteMapper
import com.topiichat.app.features.valid_phone_number.data.repo.ValidPhoneRepositoryImpl
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
class ValidPhoneRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestAppDispatchers()
    private val apiService = mock<ApiService>()
    private val verifyPhoneRemoteMapper = VerifyPhoneRemoteMapper()
    private val errorParser = ErrorParser()

    @ExperimentalCoroutinesApi
    private val repository = ValidPhoneRepositoryImpl(
        ValidPhoneRemoteDataStore(apiService),
        verifyPhoneRemoteMapper,
        testDispatcher
    )

    @ExperimentalCoroutinesApi
    @Test
    fun verify_phone_number_success() = runTest {
        whenever(apiService.verifyPhoneNumber(any())).thenReturn(ValidPhoneNumberTestUtils.dto)
        repository.verifyPhone("", "", "").transformData {
            assertEquals(it?.authyId, ValidPhoneNumberTestUtils.dto.authyId)
            assertEquals(
                it?.phoneNumber, verifyPhoneRemoteMapper
                    .map(ValidPhoneNumberTestUtils.dto)
                    .phoneNumber
            )
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun verify_phone_number_fail() = runTest {
        val errorBody = "{\"detail\": {\"error\": \"Invalid phone number\"}}"
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.verifyPhoneNumber(any()))
            .thenThrow(HttpException(Response.error<Any>(400, errorBody)))

        kotlin.runCatching {
            repository.verifyPhone("", "", "")
        }.onFailure {
            assertEquals(errorParser.parse(it).message, "Invalid phone number")
        }
    }
}