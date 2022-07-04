package com.topiichat.app.features.otp.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.otp.domain.model.ResendOtpCodeDomain
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain

interface OtpCodeRepository {
    suspend fun validateOtpCode(authyId: String?, code: String?): ResultData<ValidOtpCodeDomain>
    suspend fun resendOtpCode(authyId: String?): ResultData<ResendOtpCodeDomain>
}