package com.topiichat.app.features.home.domain.usecase

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.repo.HomeRepository
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import javax.inject.Inject

class GetAvailableCountriesUseCase @Inject constructor(
    private val getAuthData: GetAuthDataUseCase,
    private val homeRepository: HomeRepository
) : UseCase<GetAvailableCountriesUseCase.Params, AvailableCountriesDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<AvailableCountriesDomain> {
        val authDataResult = getAuthData()
        return homeRepository.getAvailableCountries(authDataResult.accessToken)
    }

    object Params
}