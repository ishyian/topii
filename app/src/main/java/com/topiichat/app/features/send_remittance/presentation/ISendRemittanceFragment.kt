package com.topiichat.app.features.send_remittance.presentation

import android.widget.AutoCompleteTextView
import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.presentation.model.BtnSendEnablingUi
import com.topiichat.app.features.send_remittance.presentation.model.RecentUsersUiModel
import com.topiichat.app.features.send_remittance.presentation.model.RecipientUiModel
import com.topiichat.app.features.send_remittance.presentation.model.SendRemittanceUIModel
import com.topiichat.core.presentation.platform.IBaseFragment

interface ISendRemittanceFragment : IBaseFragment {
    fun initCurrencyPickers(content: SendRemittanceUIModel)
    fun onReceiverCountryChanged(receiverCountry: CountryDomain)
    fun onReceiverSumChanged(amount: Double)
    fun onEnablingBtnSend(btnSendEnablingUi: BtnSendEnablingUi)
    fun onRemittancePurposesLoaded(purposes: List<RemittancePurposeDomain>?): AutoCompleteTextView
    fun onRecentUsersLoaded(recentUsersModel: RecentUsersUiModel)
    fun onVisibilityReceiverSumLoader(isVisibleLoader: Boolean)
    fun onFxRateChanged(fxRate: FxRateDomain)
    fun onSendingSumChanged(amount: Double)
    fun onRecipientSelected(recipientUiModel: RecipientUiModel)
    fun onZeroTotalAmount(senderCurrency: String)
}