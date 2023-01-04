package com.topiichat.app.features.personal_information.data.repo

import com.topiichat.app.features.personal_information.data.datasource.PersonalInfoRemoteDataSource
import com.topiichat.app.features.personal_information.domain.repo.PersonalInfoRepository
import com.topiichat.app.features.registration.data.mapper.RegisterRemoteMapper
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.core.coroutines.AppDispatchers
import com.topiichat.core.domain.ResultData
import kotlinx.coroutines.withContext

class PersonalInfoRepositoryImpl(
    private val updatePersonalInfoRemoteDataSource: PersonalInfoRemoteDataSource,
    private val registerRemoteMapper: RegisterRemoteMapper,
    private val appDispatchers: AppDispatchers
) : PersonalInfoRepository {

    override suspend fun updatePersonalInfo(
        accessToken: String,
        firstName: String?,
        lastName: String?,
        firstNameSecond: String?,
        lastNameSecond: String?
    ): ResultData<RegisterDomain> {
        return withContext(appDispatchers.network) {
            updatePersonalInfoRemoteDataSource.updateProfile(
                accessToken = accessToken,
                firstName = firstName,
                lastName = lastName,
                firstNameSecond = firstNameSecond,
                lastNameSecond = lastNameSecond
            ).transformData {
                registerRemoteMapper.map(it)
            }
        }
    }
}