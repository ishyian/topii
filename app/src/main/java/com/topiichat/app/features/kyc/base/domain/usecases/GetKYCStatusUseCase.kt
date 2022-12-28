package com.topiichat.app.features.kyc.base.domain.usecases

import com.topiichat.app.features.kyc.base.domain.model.KYCStatus
import com.topiichat.app.features.kyc.base.domain.repo.KYCRepository
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import javax.inject.Inject

class GetKYCStatusUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val kycRepository: KYCRepository
) : UseCase<GetKYCStatusUseCase.Params, KYCStatus> {

    override suspend operator fun invoke(params: Params?): ResultData<KYCStatus> {
        return params?.accessToken?.let {
            kycRepository.getKYCStatus("Bearer $it")
        } ?: kycRepository.getKYCStatus(getToken().accessToken)
    }

    data class Params(
        val accessToken: String? = null
    )
}