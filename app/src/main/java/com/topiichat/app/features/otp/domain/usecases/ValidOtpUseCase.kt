package com.topiichat.app.features.otp.domain.usecases

import com.topiichat.app.BuildConfig
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain
import com.topiichat.app.features.otp.domain.repo.OtpCodeRepository
import javax.inject.Inject

class ValidOtpUseCase @Inject constructor(
    private val otpCodeRepository: OtpCodeRepository
) : UseCase<ValidOtpUseCase.Params, ValidOtpCodeDomain> {

    private val devValidOtpCodeDomain = ValidOtpCodeDomain(
        isSuccessful = true,
        message = "Otp code valid"
    )

    override suspend operator fun invoke(params: Params?): ResultData<ValidOtpCodeDomain> {
        if (isDevOtpCode(params?.otpCode)) {
            return ResultData.Success(devValidOtpCodeDomain)
        }
        return otpCodeRepository.validateOtpCode(params?.authyId, params?.otpCode)
    }

    data class Params(
        val authyId: String,
        val otpCode: String
    )

    private fun isDevOtpCode(otpCode: String?): Boolean {
        return BuildConfig.FLAVOR == "dev" && otpCode == "111111"
    }
}