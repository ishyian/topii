package com.topiichat.app.features.kyc.base.domain.model

import com.topiichat.core.domain.Domain

enum class KYCStatus : Domain {
    KYC_VERIFIED,
    KYC_NOT_VERIFIED
}