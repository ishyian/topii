package com.topiichat.app.features.request_remittance.presentation

import android.widget.AutoCompleteTextView
import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.request_remittance.presentation.model.BtnRequestEnablingUi
import com.topiichat.app.features.request_remittance.presentation.model.SenderUiModel
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.presentation.model.RecentUsersUiModel
import com.topiichat.core.presentation.platform.IBaseFragment

interface IRequestRemittanceFragment : IBaseFragment {
    fun onEnablingBtnSend(btnRequestEnablingUi: BtnRequestEnablingUi)
    fun onRecentUsersLoaded(recentUsersModel: RecentUsersUiModel)
    fun onRemittancePurposesLoaded(purposes: List<RemittancePurposeDomain>?): AutoCompleteTextView
    fun onReceiverCountryChanged(receiverCountry: CountryDomain)
    fun onSenderSelected(senderUiModel: SenderUiModel)
}