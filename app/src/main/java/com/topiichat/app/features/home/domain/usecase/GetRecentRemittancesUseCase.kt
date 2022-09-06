package com.topiichat.app.features.home.domain.usecase

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.home.domain.repo.HomeRepository
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import javax.inject.Inject

class GetRecentRemittancesUseCase @Inject constructor(
    private val getToken: GetAuthDataUseCase,
    private val homeRepository: HomeRepository
) : UseCase<GetRecentRemittancesUseCase.Params, List<RecentUserDomain>> {

    override suspend operator fun invoke(params: Params?): ResultData<List<RecentUserDomain>> {
        val tokenResult = getToken()
        return homeRepository.getRecentUsers(
            token = tokenResult.accessToken
        )
    }

    object Params
}