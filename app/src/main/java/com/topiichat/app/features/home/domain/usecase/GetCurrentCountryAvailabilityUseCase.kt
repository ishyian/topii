package com.topiichat.app.features.home.domain.usecase

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.domain.UseCase
import com.topiichat.app.features.home.domain.model.CurrentCountryDomain
import com.topiichat.app.features.home.domain.repo.HomeRepository
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import timber.log.Timber
import javax.inject.Inject

class GetCurrentCountryAvailabilityUseCase @Inject constructor(
    private val getAuthData: GetAuthDataUseCase,
    private val homeRepository: HomeRepository
) : UseCase<GetCurrentCountryAvailabilityUseCase.Params, CurrentCountryDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<CurrentCountryDomain> {
        val authDataResult = getAuthData()
        return when (val result = homeRepository.getAvailableCountries(authDataResult.accessToken)) {
            is ResultData.Success -> {
                Timber.d("registered iso code = ${authDataResult.isoCode}")
                Timber.d("available countries codes = ${result.data.countries.joinToString { it.code }}")
                if (result.data.countries.any { it.code == authDataResult.isoCode }) {
                    val country = result.data.countries.find { it.code == authDataResult.isoCode }
                    ResultData.Success(CurrentCountryDomain(true, country))
                } else ResultData.Success(CurrentCountryDomain(false, null))
            }
            is ResultData.Fail -> {
                Timber.d("result failed ${result.error.message}")
                ResultData.Success(CurrentCountryDomain(false, null))
            }
            is ResultData.NetworkError -> {
                ResultData.Success(CurrentCountryDomain(false, null))
            }
        }
    }

    object Params
}