package com.topiichat.app.features.splash.data.repo

import com.topiichat.app.features.splash.data.datasource.ValidateAppRemoteDataSource
import com.topiichat.app.features.splash.data.mapper.ValidateAppRemoteMapper
import com.topiichat.app.features.splash.domain.model.ValidateAppDomain
import com.topiichat.app.features.splash.domain.repo.ValidateAppRepository
import com.topiichat.core.coroutines.AppDispatchers
import com.topiichat.core.domain.ResultData
import kotlinx.coroutines.withContext

class ValidateAppRepositoryImpl(
    private val validateAppDataStore: ValidateAppRemoteDataSource,
    private val validateAppRemoteMapper: ValidateAppRemoteMapper,
    private val appDispatchers: AppDispatchers
) : ValidateAppRepository {
    override suspend fun validateApp(accessToken: String): ResultData<ValidateAppDomain> {
        return withContext(appDispatchers.network) {
            validateAppDataStore.validateApp(accessToken).transformData {
                validateAppRemoteMapper.map(it)
            }
        }
    }
}