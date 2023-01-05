package com.topiichat.app.features.splash.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.features.splash.data.model.ValidateAppDto
import com.topiichat.core.data.datasource.BaseRemoteDataStore
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class ValidateAppRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun validateApp(
        accessToken: String
    ): ResultData<ValidateAppDto> {
        return safeApiCall {
            apiService.validateApp(accessToken)
        }
    }

}