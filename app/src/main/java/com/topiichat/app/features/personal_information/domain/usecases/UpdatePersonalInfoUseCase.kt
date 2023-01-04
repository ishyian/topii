package com.topiichat.app.features.personal_information.domain.usecases

import com.topiichat.app.features.personal_information.domain.repo.PersonalInfoRepository
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.core.domain.ResultData
import com.topiichat.core.domain.UseCase
import javax.inject.Inject

class UpdatePersonalInfoUseCase @Inject constructor(
    private val getAuthData: GetAuthDataUseCase,
    private val personalInfoRepository: PersonalInfoRepository
) : UseCase<UpdatePersonalInfoUseCase.Params, RegisterDomain> {

    override suspend operator fun invoke(params: Params?): ResultData<RegisterDomain> {
        val authData = getAuthData()
        return personalInfoRepository.updatePersonalInfo(
            authData.accessToken,
            firstName = params?.firstName,
            lastName = params?.lastName,
            firstNameSecond = params?.firstNameSecond,
            lastNameSecond = params?.lastNameSecond
        )
    }

    data class Params(
        var firstName: String? = null,
        var lastName: String? = null,
        var firstNameSecond: String? = null,
        var lastNameSecond: String? = null
    )
}