package com.topiichat.app.data.features.valid_phone_number.data.mapper

import com.topiichat.app.data.core.MainDispatcherRule
import com.topiichat.app.data.features.valid_phone_number.utils.ValidPhoneNumberTestUtils
import com.topiichat.app.features.valid_phone_number.data.mapper.VerifyPhoneRemoteMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class VerifyPhoneRemoteMapperTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    private val remoteMapper = VerifyPhoneRemoteMapper()

    @ExperimentalCoroutinesApi
    @Test
    fun mapper_return_right_data() = runTest {
        val testDto = ValidPhoneNumberTestUtils.dto
        val testDomain = ValidPhoneNumberTestUtils.domain

        val result = remoteMapper.map(testDto)

        assertEquals(
            "Remote Dto authyId is not same as Domain authyId",
            result.authyId,
            testDomain.authyId
        )
        assertEquals(
            "Remote Dto phone number is not same as Domain phone number",
            result.phoneNumber,
            testDomain.phoneNumber
        )
        assertEquals(
            "Remote Dto dialCode is not same as Domain dialCode",
            result.code,
            testDomain.code
        )
    }
}