package com.topiichat.app.features.otp.domain.repo

import com.topiichat.app.features.otp.domain.model.ResendOtpCodeDomain
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain
import com.topiichat.core.domain.ResultData

interface OtpCodeRepository {
    suspend fun validateOtpCode(authyId: String?, code: String?): ResultData<ValidOtpCodeDomain>
    suspend fun resendOtpCode(authyId: String?): ResultData<ResendOtpCodeDomain>
}