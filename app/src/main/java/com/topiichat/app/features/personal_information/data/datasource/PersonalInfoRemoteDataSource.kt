package com.topiichat.app.features.personal_information.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.features.personal_information.data.model.UpdateProfileInfoDto
import com.topiichat.app.features.registration.data.model.RegisterDto
import com.topiichat.core.data.datasource.BaseRemoteDataStore
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class PersonalInfoRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun updateProfile(
        accessToken: String,
        firstName: String? = null,
        lastName: String? = null,
        firstNameSecond: String? = null,
        lastNameSecond: String? = null
    ): ResultData<RegisterDto> {
        return safeApiCall {
            apiService.updateProfile(
                accessToken,
                UpdateProfileInfoDto(
                    firstName, lastName, firstNameSecond, lastNameSecond
                )
            )
        }
    }

}