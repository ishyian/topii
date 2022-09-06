package com.topiichat.app.features.kyc.base.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.kyc.base.domain.model.KYCStatus

interface KYCRepository {
    suspend fun getKYCStatus(accessToken: String): ResultData<KYCStatus>
}