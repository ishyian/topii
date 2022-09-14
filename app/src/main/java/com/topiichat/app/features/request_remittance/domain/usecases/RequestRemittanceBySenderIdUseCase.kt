package com.topiichat.app.features.request_remittance.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.core.exception.domain.emitError
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.request_remittance.domain.model.RequestRemittanceDomain
import com.topiichat.app.features.request_remittance.domain.repo.RequestRemittanceRepository
import javax.inject.Inject

class RequestRemittanceBySenderIdUseCase
@Inject constructor(
    private val getAuthData: GetAuthDataUseCase,
    private val repository: RequestRemittanceRepository
) : UseCase<RequestRemittanceBySenderIdUseCase.Params, RequestRemittanceDomain> {
    override suspend fun invoke(params: Params?): ResultData<RequestRemittanceDomain> {
        val authData = getAuthData()
        return params?.let {
            return repository.requestRemittanceBySenderId(
                authData.accessToken,
                it.senderId,
                it.purposeCode,
                it.description,
                it.fromCurrencyCode,
                it.fromCountryCode,
                it.toCurrencyCode,
                it.toCountryCode,
                it.amount
            )
        } ?: ResultData.Fail(emitError("RequestRemittanceBySenderIdUseCase params are null"))
    }

    data class Params(
        val senderId: String,
        val purposeCode: String,
        val description: String,
        val fromCurrencyCode: String,
        val fromCountryCode: String,
        val toCurrencyCode: String,
        val toCountryCode: String,
        val amount: Double
    )
}