package com.topiichat.app.features.kyc.base.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.features.kyc.base.data.datasource.KYCRemoteDataSource
import com.topiichat.app.features.kyc.base.data.mapper.KYCStatusRemoteMapper
import com.topiichat.app.features.kyc.base.domain.repo.KYCRepository
import kotlinx.coroutines.withContext

class KYCRepositoryImpl(
    private val kycRemoteDataSource: KYCRemoteDataSource,
    private val kycStatusRemoteMapper: KYCStatusRemoteMapper,
    private val appDispatchers: AppDispatchers
) : KYCRepository {
    override suspend fun getKYCStatus(accessToken: String) = withContext(appDispatchers.network) {
        kycRemoteDataSource.getKYCStatus(accessToken).transformData {
            kycStatusRemoteMapper.map(it)
        }
    }
}