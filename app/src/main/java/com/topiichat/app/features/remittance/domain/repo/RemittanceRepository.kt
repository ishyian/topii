package com.topiichat.app.features.remittance.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain

interface RemittanceRepository {
    suspend fun getRemittanceDetail(
        accessToken: String,
        remittanceId: String
    ): ResultData<RemittanceDomain>
}