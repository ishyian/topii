package com.topiichat.app.features.kyc.base.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.kyc.base.domain.model.KYCStatus
import com.topiichat.app.features.kyc.base.domain.model.TokenAliceDomain

interface KYCRepository {
    suspend fun getKYCStatus(accessToken: String): ResultData<KYCStatus>
    suspend fun getTokenAlice(email: String, firstName: String, lastName: String): ResultData<TokenAliceDomain>
}