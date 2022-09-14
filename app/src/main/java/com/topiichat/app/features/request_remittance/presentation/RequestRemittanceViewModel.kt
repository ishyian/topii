package com.topiichat.app.features.request_remittance.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.home.domain.usecase.GetAvailableCountriesUseCase
import com.topiichat.app.features.home.domain.usecase.GetRecentRemittancesUseCase
import com.topiichat.app.features.request_remittance.domain.model.RemittanceType
import com.topiichat.app.features.request_remittance.domain.model.RequestRemittanceDomain
import com.topiichat.app.features.request_remittance.domain.usecases.RequestRemittanceByPhoneUseCase
import com.topiichat.app.features.request_remittance.domain.usecases.RequestRemittanceBySenderIdUseCase
import com.topiichat.app.features.request_remittance.presentation.model.BtnRequestEnablingUi
import com.topiichat.app.features.request_remittance.presentation.model.SenderUiModel
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.domain.usecases.GetRemittancePurposesUseCase
import com.topiichat.app.features.send_remittance.presentation.mapper.RecentUsersUiMapper
import com.topiichat.app.features.send_remittance.presentation.model.RecentUserUiModel
import com.topiichat.app.features.send_remittance.presentation.model.RecentUsersUiModel
import com.topiichat.app.features.send_remittance.presentation.model.SendRemittanceUIModel
import com.topiichat.app.features.send_remittance.presentation.model.changeCheckedStatus
import com.topiichat.app.features.valid_phone_number.presentation.model.PhoneNumber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class RequestRemittanceViewModel @AssistedInject constructor(
    @Assisted private val parameters: RequestRemittanceParameters,
    private val getRemittancePurposes: GetRemittancePurposesUseCase,
    private val getAvailableCountries: GetAvailableCountriesUseCase,
    private val getRecentRemittances: GetRecentRemittancesUseCase,
    private val requestRemittanceByPhone: RequestRemittanceByPhoneUseCase,
    private val requestRemittanceBySenderId: RequestRemittanceBySenderIdUseCase,
    private val recentUsersUiMapper: RecentUsersUiMapper,
    appRouter: Router
) : BaseViewModel(appRouter), IRequestRemittanceViewModel {

    private val _content: MutableLiveData<SendRemittanceUIModel> = MutableLiveData()
    val content: LiveData<SendRemittanceUIModel> = _content

    private var _receiverCountry = MutableLiveData<CountryDomain>()
    val receiverCountry: LiveData<CountryDomain> = _receiverCountry

    private var _remittancePurposes = MutableLiveData<List<RemittancePurposeDomain>>()
    val remittancePurposes = _remittancePurposes

    private var _recentUsers = MutableLiveData<RecentUsersUiModel>()
    val recentUsers = _recentUsers

    private val _btnRequestEnabling: MutableLiveData<BtnRequestEnablingUi> = MutableLiveData()
    val btnRequestEnabling: LiveData<BtnRequestEnablingUi> = _btnRequestEnabling

    private var _showContactDataInput = MutableLiveData<Unit>()
    val showContactDataInput = _showContactDataInput

    private val _availableCountries = MutableLiveData<AvailableCountriesDomain>()
    val availableCountries: LiveData<AvailableCountriesDomain> = _availableCountries

    private var _senderModel = MutableLiveData<SenderUiModel>()
    val senderModel = _senderModel

    var phoneNumber: PhoneNumber? = null

    private var requestAmount = 0.0
    private var senderId: String = ""
    private var senderCurrencyCode = ""
    private var senderCountryCode = ""
    private var purposeCode: String = ""
    private var description: String = ""
    private var contactName: String = ""
    private var isCheckedSwitch = false

    private var remittanceType = RemittanceType.SENDER_ID

    init {
        _btnRequestEnabling.value = BtnRequestEnablingUi(
            isEnabled = false,
            backgroundId = R.drawable.bg_button_unenabled
        )

        viewModelScope.launch {
            _showLoader.value = true
            when (val result = getAvailableCountries()) {
                is ResultData.Success -> {
                    result.data.let {
                        _availableCountries.value = it
                        _receiverCountry.value = parameters.country
                    }
                    loadRemittancePurposes()
                    loadRecentUsers()
                }
                is ResultData.Fail -> {
                    _showLoader.value = false
                    _showMsgError.postValue(result.error.message)
                }
            }
            _showLoader.value = false
        }
    }

    override suspend fun loadRemittancePurposes() {
        when (val purposesResult = getRemittancePurposes()) {
            is ResultData.Success -> {
                onSuccessRemittancePurposes(purposesResult.data)
            }
            else -> {
                //Nothing for now
            }
        }
    }

    override suspend fun loadRecentUsers() {
        when (val recentUsers = getRecentRemittances()) {
            is ResultData.Success -> {
                onSuccessRecentUsers(recentUsers.data)
            }
            else -> {
                //Nothing for now
            }
        }
    }

    override fun onDescriptionChanged(description: String) {
        this.description = description
    }

    override fun onSuccessRecentUsers(recentUsers: List<RecentUserDomain>?) {
        _recentUsers.postValue(recentUsersUiMapper.map(recentUsers))
    }

    override fun onSuccessRemittancePurposes(purposes: List<RemittancePurposeDomain>?) {
        _remittancePurposes.postValue(purposes ?: emptyList())
        purposeCode = purposes?.firstOrNull()?.value ?: ""
        onUpdateBtnRequest()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_view_back -> onClickBack()
            R.id.btn_request -> onRequestClick()
        }
    }

    override fun onRequestClick() {
        viewModelScope.launch {
            _showLoader.value = true
            val result = when (remittanceType) {
                RemittanceType.SENDER_ID -> requestBySenderId()
                RemittanceType.PHONE_NUMBER -> requestByPhone()
            }
            when (result) {
                is ResultData.Success -> {
                    onSuccessRequestRemittance()
                }
                is ResultData.Fail -> {
                    onFailRequestRemittance()
                }
            }
            _showLoader.value = false
        }
    }

    private suspend fun requestByPhone(): ResultData<RequestRemittanceDomain> {
        return requestRemittanceByPhone(
            RequestRemittanceByPhoneUseCase.Params(
                senderDialCode = phoneNumber?.code ?: "",
                senderNumber = phoneNumber?.number ?: "",
                purposeCode = purposeCode,
                description = description,
                fromCurrencyCode = parameters.country.currencyCode,
                fromCountryCode = parameters.country.code,
                toCurrencyCode = senderCurrencyCode,
                toCountryCode = senderCountryCode,
                amount = requestAmount
            )
        )
    }

    private suspend fun requestBySenderId(): ResultData<RequestRemittanceDomain> {
        return requestRemittanceBySenderId(
            RequestRemittanceBySenderIdUseCase.Params(
                senderId = senderId,
                purposeCode = purposeCode,
                description = description,
                fromCurrencyCode = parameters.country.currencyCode,
                fromCountryCode = parameters.country.code,
                toCurrencyCode = senderCurrencyCode,
                toCountryCode = senderCountryCode,
                amount = requestAmount
            )
        )
    }

    private fun onFailRequestRemittance() {
        onFailRequestRemittance()
    }

    private fun onSuccessRequestRemittance() {
        _showMsgError.postValue("Request sent successfully")
        onClickBack()
    }

    override fun onAmountChanged(amount: Double) {
        requestAmount = amount
        onUpdateBtnRequest()
    }

    override fun onRecentUserClick(recentUser: RecentUserUiModel) {
        val newModel = recentUsers.value?.changeCheckedStatus(recentUser)
        newModel?.let { _recentUsers.value = it }
        senderId = recentUser.data.recipientId
        val senderCountry =
            availableCountries
                .value
                ?.countries
                ?.firstOrNull { "+${it.dialCountryCode}" == recentUser.data.dialCode }
        senderCountryCode = senderCountry?.code ?: ""
        senderCurrencyCode = senderCountry?.currencyCode ?: ""

        val senderUiModel = SenderUiModel(
            userData = recentUser.data,
            senderCountry = senderCountry
        )
        _senderModel.value = senderUiModel
        onUpdateBtnRequest()
    }

    override fun onAddUserClick() {
        remittanceType = RemittanceType.PHONE_NUMBER
        _showContactDataInput.value = Unit
    }

    override fun onUpdateBtnRequest() {
        val isValidFields = isCheckedSwitch &&
            requestAmount > 0.0 &&
            purposeCode.isNotEmpty()

        val isEnabled = when (remittanceType) {
            RemittanceType.PHONE_NUMBER -> isValidFields &&
                phoneNumber?.number != null && phoneNumber?.code != null
            RemittanceType.SENDER_ID -> isValidFields &&
                senderId.isNotEmpty()
        }

        val btnRequestEnabling = if (isEnabled) {
            BtnRequestEnablingUi(
                isEnabled = true,
                backgroundId = R.drawable.bg_button
            )
        } else {
            BtnRequestEnablingUi(
                isEnabled = false,
                backgroundId = R.drawable.bg_button_unenabled
            )
        }
        _btnRequestEnabling.value = btnRequestEnabling
    }

    override fun onPurposeNotSelected() {
        purposeCode = ""
        onUpdateBtnRequest()
    }

    override fun onPurposeSelected(position: Int) {
        purposeCode = remittancePurposes.value?.get(position)?.value ?: ""
        onUpdateBtnRequest()
    }

    override fun onCheckedChanged(id: Int?, isChecked: Boolean) {
        if (id == null) return
        when (id) {
            R.id.switch_payment -> {
                isCheckedSwitch = isChecked
            }
        }
        onUpdateBtnRequest()
    }

    override fun onContactNameChanged(contactName: String) {
        this.contactName = contactName
    }

    override fun onPhoneNumberChanged(phoneNumber: PhoneNumber) {
        this.phoneNumber = phoneNumber
        onUpdateBtnRequest()
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted parameters: RequestRemittanceParameters
        ): RequestRemittanceViewModel
    }
}