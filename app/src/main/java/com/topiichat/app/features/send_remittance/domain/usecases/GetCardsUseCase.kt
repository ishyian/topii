package com.topiichat.app.features.send_remittance.domain.usecases

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.send_remittance.domain.model.CardDomain
import com.topiichat.app.features.send_remittance.domain.repo.SendRemittanceRepository
import javax.inject.Inject

class GetCardsUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val sendRemittanceRepository: SendRemittanceRepository
) : UseCase<GetCardsUseCase.Params, List<CardDomain>> {

    override suspend operator fun invoke(params: Params?): ResultData<List<CardDomain>> {
        val tokenResult = getToken()
        return sendRemittanceRepository.getCards(tokenResult.accessToken)
    }

    object Params
}