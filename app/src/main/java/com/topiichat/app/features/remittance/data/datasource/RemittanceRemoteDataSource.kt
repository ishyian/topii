package com.topiichat.app.features.remittance.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.features.send_remittance.data.model.RemittanceDto
import com.topiichat.core.data.datasource.BaseRemoteDataStore
import com.topiichat.core.domain.ResultData
import javax.inject.Inject

class RemittanceRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {

    suspend fun getRemittanceDetail(
        accessToken: String,
        remittanceId: String
    ): ResultData<RemittanceDto> {
        return safeApiCall {
            apiService.getRemittanceId(
                accessToken = accessToken,
                remittanceId = remittanceId
            )
        }
    }
}