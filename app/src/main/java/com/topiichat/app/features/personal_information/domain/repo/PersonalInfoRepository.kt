package com.topiichat.app.features.personal_information.domain.repo

import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.core.domain.ResultData

interface PersonalInfoRepository {
    suspend fun updatePersonalInfo(
        firstName: String?,
        lastName: String?,
        firstNameSecond: String?,
        lastNameSecond: String?
    ): ResultData<RegisterDomain>
}