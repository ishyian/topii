package com.topiichat.app.features.kyc.base.data.mapper

import com.topiichat.app.core.domain.Mapper
import com.topiichat.app.features.kyc.base.data.model.KYCStatusDto
import com.topiichat.app.features.kyc.base.domain.model.KYCStatus
import javax.inject.Inject

class KYCStatusRemoteMapper @Inject constructor() : Mapper<KYCStatusDto, KYCStatus> {
    override fun map(input: KYCStatusDto?): KYCStatus {
        return if (input == null) KYCStatus.KYC_NOT_VERIFIED
        else when (input.status) {
            NOT_VERIFIED -> KYCStatus.KYC_NOT_VERIFIED
            else -> KYCStatus.KYC_VERIFIED
        }
    }

    companion object {
        const val NOT_VERIFIED = "not_kyc"
    }
}