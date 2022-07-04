package com.topiichat.app.data.features.registration.data.mapper

import com.topiichat.app.data.core.MainDispatcherRule
import com.topiichat.app.data.features.registration.utils.RegisterTestUtils
import com.topiichat.app.features.registration.data.mapper.RegisterRemoteMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RegisterRemoteMapperTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    private val remoteMapper = RegisterRemoteMapper()

    @ExperimentalCoroutinesApi
    @Test
    fun mapper_return_right_data() = runTest {
        val testDto = RegisterTestUtils.dto
        val testDomain = RegisterTestUtils.domain

        val result = remoteMapper.map(testDto)

        assertEquals(
            "Remote Dto accessToken is not same as Domain accessToken",
            result.accessToken,
            testDomain.accessToken
        )
    }
}