package com.topiichat.app.features.kyc.base.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.kyc.base.domain.model.KYCStatus
import com.topiichat.app.features.kyc.base.domain.repo.KYCRepository
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import javax.inject.Inject

class GetKYCStatusUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val kycRepository: KYCRepository
) : UseCase<GetKYCStatusUseCase.Params, KYCStatus> {

    override suspend operator fun invoke(params: Params?): ResultData<KYCStatus> {
        val tokenResult = getToken()
        //return kycRepository.getKYCStatus(tokenResult.accessToken)
        return ResultData.Success(KYCStatus.KYC_VERIFIED)
    }

    object Params
}