package com.topiichat.app.features.home.presentation

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.home.domain.model.RemittanceDomain
import com.topiichat.app.features.kyc.base.domain.model.KYCStatus

interface IHomeViewModel {
    fun onSendPaymentClick()
    fun onTransactionClick(remittanceDomain: RemittanceDomain)
    fun onFiltersClick()
    fun onChatsClick()
    fun checkAvailableCountryFeatures()
    fun loadRemmittanceHistory(month: Int)
    fun onFailRemmitanceHistory(failure: ResultData.Fail)
    fun onKYCStatusStatus(status: KYCStatus?)
}