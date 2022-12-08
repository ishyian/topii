package com.topiichat.app.features.request_remittance.presentation

import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.presentation.model.RecentUserUiModel
import com.topiichat.app.features.valid_phone_number.presentation.model.PhoneNumber
import com.topiichat.core.presentation.platform.IBaseViewModel

interface IRequestRemittanceViewModel : IBaseViewModel {
    suspend fun loadRemittancePurposes()
    suspend fun loadRecentUsers()
    fun onDescriptionChanged(description: String)
    fun onSuccessRecentUsers(recentUsers: List<RecentUserDomain>?)
    fun onSuccessRemittancePurposes(purposes: List<RemittancePurposeDomain>?)
    fun onAmountChanged(amount: Double)
    fun onRecentUserClick(recentUser: RecentUserUiModel)
    fun onAddUserClick()
    fun onUpdateBtnRequest()
    fun onPurposeNotSelected()
    fun onPurposeSelected(position: Int)
    fun onCheckedChanged(id: Int?, isChecked: Boolean)
    fun onRequestClick()
    fun onPhoneNumberChanged(phoneNumber: PhoneNumber)
    fun onContactNameChanged(contactName: String)
    fun onSuccessRequestRemittance()
}