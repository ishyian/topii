package com.topiichat.app.data.features.registration.data.repo

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.data.core.MainDispatcherRule
import com.topiichat.app.data.core.TestAppDispatchers
import com.topiichat.app.data.features.registration.utils.RegisterTestUtils
import com.topiichat.app.features.registration.data.datasource.cache.RegisterCache
import com.topiichat.app.features.registration.data.datasource.cache.RegisterCacheDataStore
import com.topiichat.app.features.registration.data.datasource.remote.RegisterRemoteDataStore
import com.topiichat.app.features.registration.data.mapper.RegisterCacheMapper
import com.topiichat.app.features.registration.data.mapper.RegisterRemoteMapper
import com.topiichat.app.features.registration.data.repo.RegisterRepositoryImpl
import com.topiichat.core.data.EmptyMapper
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
class RegisterRepositoryTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    private val testDispatcher = TestAppDispatchers()
    private val apiService = mock<ApiService>()
    private val registerCache = mock<RegisterCache>()

    private val registerRemoteMapper = RegisterRemoteMapper()
    private val registerCacheMapper = RegisterCacheMapper()
    private val emptyMapper = EmptyMapper()

    private val errorParser = ErrorParser()

    @ExperimentalCoroutinesApi
    private val repository = RegisterRepositoryImpl(
        registerCacheDataStore = RegisterCacheDataStore(registerCache = registerCache),
        registerRemoteDataStore = RegisterRemoteDataStore(apiService = apiService),
        registerRemoteMapper = registerRemoteMapper,
        registerCacheMapper = registerCacheMapper,
        emptyMapper = emptyMapper,
        appDispatchers = testDispatcher
    )

    @ExperimentalCoroutinesApi
    @Test
    fun register_success() = runTest {
        whenever(apiService.register(any())).thenReturn(RegisterTestUtils.dto)
        repository.register("", "", "", "", "")
            .transformData {
                assertEquals(it.accessToken, RegisterTestUtils.dto.accessToken)
            }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun register_fail() = runTest {
        val errorBody = "{\"detail\": {\"error\": \"Register failed\"}}"
            .toResponseBody("application/json".toMediaTypeOrNull())

        whenever(apiService.register(any()))
            .thenThrow(HttpException(Response.error<Any>(400, errorBody)))

        kotlin.runCatching {
            repository.register("", "", "", "", "")
        }.onFailure {
            assertEquals(errorParser.parse(it).message, "Register failed")
        }
    }
}