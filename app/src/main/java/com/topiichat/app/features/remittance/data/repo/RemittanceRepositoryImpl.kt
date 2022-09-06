package com.topiichat.app.features.remittance.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.remittance.data.datasource.RemittanceRemoteDataSource
import com.topiichat.app.features.remittance.domain.repo.RemittanceRepository
import com.topiichat.app.features.send_remittance.data.mapper.RemittanceRemoteMapper
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import kotlinx.coroutines.withContext

class RemittanceRepositoryImpl(
    private val remittanceRemoteDataSource: RemittanceRemoteDataSource,
    private val remittanceRemoteMapper: RemittanceRemoteMapper,
    private val appDispatchers: AppDispatchers
) : RemittanceRepository {
    override suspend fun getRemittanceDetail(accessToken: String, remittanceId: String): ResultData<RemittanceDomain> {
        return withContext(appDispatchers.network) {
            remittanceRemoteDataSource.getRemittanceDetail(accessToken, remittanceId)
                .transformData {
                    remittanceRemoteMapper.map(it)
                }
        }
    }
}