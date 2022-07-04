package com.topiichat.app.data.features.otp.data.mapper

import com.topiichat.app.data.core.MainDispatcherRule
import com.topiichat.app.data.features.otp.utils.OtpCodeTestUtils
import com.topiichat.app.features.otp.data.mapper.ResendOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.mapper.ValidOtpCodeRemoteMapper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class OtpRemoteMapperTest {

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule: MainDispatcherRule = MainDispatcherRule()

    private val validOtpRemoteMapper = ValidOtpCodeRemoteMapper()
    private val resendOtpRemoteMapper = ResendOtpCodeRemoteMapper()

    @ExperimentalCoroutinesApi
    @Test
    fun otp_mappers_return_right_data() = runTest {
        val validOtpTestDto = OtpCodeTestUtils.validOtpDto
        val validOtpTestDomain = OtpCodeTestUtils.validOtpDomain

        val resendOtpTestDto = OtpCodeTestUtils.resendOtpDto
        val resendOtpCodeTestDomain = OtpCodeTestUtils.resendOtpDomain

        val resultValidOtp = validOtpRemoteMapper.map(validOtpTestDto)
        val resultResendOtp = resendOtpRemoteMapper.map(resendOtpTestDto)

        assertEquals(
            "Remote Valid Otp Dto success is not same as Domain isSuccessful",
            resultValidOtp.isSuccessful,
            validOtpTestDomain.isSuccessful
        )
        assertEquals(
            "Remote Valid Otp Dto message is not same as Domain message",
            resultValidOtp.message,
            validOtpTestDomain.message
        )
        assertEquals(
            "Remote Resend Otp Dto success is not same as Domain isSuccessful",
            resultResendOtp.isSuccessful,
            resendOtpCodeTestDomain.isSuccessful
        )
        assertEquals(
            "Remote Valid Otp Dto message is not same as Domain message",
            resultResendOtp.message,
            resendOtpCodeTestDomain.message
        )
    }
}