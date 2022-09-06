package com.topiichat.app.features.send_remittance.presentation

import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.presentation.model.RecentUserUiModel

interface ISendRemittanceViewModel {
    fun onAddRecipientClick()
    fun onToCurrencyChanged(countryDomain: CountryDomain)
    fun onAmountChanged(amount: Double)
    fun getFxRateForAmount(amount: Double, updateOnlyReceiverSum: Boolean = false)
    fun onSuccessFxRate(fxRate: FxRateDomain?, updateOnlyReceiverSum: Boolean)
    fun onRecentUserClick(recentUser: RecentUserUiModel)
    fun onAddUserClick()
    fun onUpdateBtnSend()
    fun onPurposeNotSelected()
    fun onPurposeSelected(position: Int)
    fun onCheckedChanged(id: Int?, isChecked: Boolean)
    fun onSuccessRecentUsers(recentUsers: List<RecentUserDomain>?)
    fun onSuccessRemittancePurposes(purposes: List<RemittancePurposeDomain>?)
    suspend fun loadRemittancePurposes()
    suspend fun loadUserCard()
    suspend fun loadRecentUsers()
    fun onSendRemittanceClick()
    fun sendRemittance()
    fun onDescriptionChanged(description: String)
}