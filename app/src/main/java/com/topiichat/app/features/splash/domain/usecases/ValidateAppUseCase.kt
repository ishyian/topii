package com.topiichat.app.features.splash.domain.usecases

import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.splash.domain.model.ValidateAppDomain
import com.topiichat.app.features.splash.domain.repo.ValidateAppRepository
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import javax.inject.Inject

class ValidateAppUseCase @Inject constructor(
    private val getAuthData: GetAuthDataUseCase,
    private val validateAppRepository: ValidateAppRepository
) : UseCase<ValidateAppUseCase.Params, ValidateAppDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<ValidateAppDomain> {
        val authData = getAuthData()
        return validateAppRepository.validateApp(authData.accessToken)
    }

    object Params
}