package com.topiichat.app.features.otp.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.otp.data.datasource.OtpCodeRemoteDataSource
import com.topiichat.app.features.otp.data.mapper.ResendOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.mapper.ValidOtpCodeRemoteMapper
import com.topiichat.app.features.otp.domain.model.ResendOtpCodeDomain
import com.topiichat.app.features.otp.domain.model.ValidOtpCodeDomain
import com.topiichat.app.features.otp.domain.repo.OtpCodeRepository
import kotlinx.coroutines.withContext

class OtpCodeRepositoryImpl(
    private val otpCodeRemoteDataSource: OtpCodeRemoteDataSource,
    private val validOtpCodeRemoteMapper: ValidOtpCodeRemoteMapper,
    private val resendOtpCodeRemoteMapper: ResendOtpCodeRemoteMapper,
    private val appDispatchers: AppDispatchers
) : OtpCodeRepository {
    override suspend fun validateOtpCode(authyId: String?, code: String?): ResultData<ValidOtpCodeDomain> {
        return withContext(appDispatchers.network) {
            otpCodeRemoteDataSource.validateOtpCode(authyId, code).transformData {
                validOtpCodeRemoteMapper.map(it)
            }
        }
    }

    override suspend fun resendOtpCode(authyId: String?): ResultData<ResendOtpCodeDomain> {
        return withContext(appDispatchers.network) {
            otpCodeRemoteDataSource.resendOtpCode(authyId).transformData {
                resendOtpCodeRemoteMapper.map(it)
            }
        }
    }
}