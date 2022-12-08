package com.topiichat.app.features.remittance.domain.repo

import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import com.topiichat.core.domain.ResultData

interface RemittanceRepository {
    suspend fun getRemittanceDetail(
        accessToken: String,
        remittanceId: String
    ): ResultData<RemittanceDomain>
}