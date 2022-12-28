package com.topiichat.app.features.home.domain.usecase

import com.topiichat.app.features.home.domain.model.RemittanceHistoryDomain
import com.topiichat.app.features.home.domain.repo.HomeRepository
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import com.topiichat.core.exception.domain.emitError
import javax.inject.Inject

class GetRemittanceHistoryUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val homeRepository: HomeRepository
) : UseCase<GetRemittanceHistoryUseCase.Params, RemittanceHistoryDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<RemittanceHistoryDomain> {
        val tokenResult = getToken()
        params?.let {
            return homeRepository.getRemittanceHistory(
                token = tokenResult.accessToken,
                year = it.year,
                month = it.month,
                senderId = tokenResult.senderId
            )
        } ?: return ResultData.Fail(emitError("FetchRemittanceHistoryUseCase params are null"))
    }

    data class Params(
        val year: Int,
        val month: Int,
        val skip: Int = 0,
        val first: Int = 0
    )
}