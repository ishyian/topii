package com.topiichat.app.features.otp.domain.usecases

import com.topiichat.app.features.otp.domain.model.ResendOtpCodeDomain
import com.topiichat.app.features.otp.domain.repo.OtpCodeRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import javax.inject.Inject

class ResendSmsUseCase @Inject constructor(
    private val otpCodeRepository: OtpCodeRepository
) : UseCase<ResendSmsUseCase.Params, ResendOtpCodeDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<ResendOtpCodeDomain> {
        return otpCodeRepository.resendOtpCode(params?.authyId)
    }

    data class Params(
        val authyId: String
    )
}