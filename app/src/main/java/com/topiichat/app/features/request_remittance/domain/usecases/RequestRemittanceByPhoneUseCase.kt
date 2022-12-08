package com.topiichat.app.features.request_remittance.domain.usecases

import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.request_remittance.domain.model.RequestRemittanceDomain
import com.topiichat.app.features.request_remittance.domain.repo.RequestRemittanceRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import com.topiichat.core.exception.domain.emitError
import javax.inject.Inject

class RequestRemittanceByPhoneUseCase
@Inject constructor(
    private val getAuthData: GetAuthDataUseCase,
    private val repository: RequestRemittanceRepository
) : UseCase<RequestRemittanceByPhoneUseCase.Params, RequestRemittanceDomain> {
    override suspend fun invoke(params: Params?): ResultData<RequestRemittanceDomain> {
        val authData = getAuthData()
        return params?.let {
            return repository.requestRemittanceByPhone(
                authData.accessToken,
                it.senderDialCode,
                it.senderNumber,
                it.purposeCode,
                it.description,
                it.fromCurrencyCode,
                it.fromCountryCode,
                it.toCurrencyCode,
                it.toCountryCode,
                it.amount
            )
        } ?: ResultData.Fail(emitError("RequestRemittanceByPhoneUseCase params are null"))
    }

    data class Params(
        val senderDialCode: String,
        val senderNumber: String,
        val purposeCode: String,
        val description: String,
        val fromCurrencyCode: String,
        val fromCountryCode: String,
        val toCurrencyCode: String,
        val toCountryCode: String,
        val amount: Double
    )
}