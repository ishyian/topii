package com.topiichat.app.features.splash.domain.repo

import com.topiichat.app.features.splash.domain.model.ValidateAppDomain
import com.topiichat.core.domain.ResultData

interface ValidateAppRepository {
    suspend fun validateApp(accessToken: String): ResultData<ValidateAppDomain>
}