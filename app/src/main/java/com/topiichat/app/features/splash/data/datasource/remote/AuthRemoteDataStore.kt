package com.topiichat.app.features.splash.data.datasource.remote

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.splash.data.model.TokenDto
import com.topiichat.app.core.data.datasource.BaseRemoteDataStore
import javax.inject.Inject

class AuthRemoteDataStore @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun fetchToken(): ResultData<TokenDto?> {
        return fetchResult {
            apiService.fetchToken()
        }
    }
}