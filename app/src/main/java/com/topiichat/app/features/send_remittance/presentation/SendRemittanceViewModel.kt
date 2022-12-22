package com.topiichat.app.features.send_remittance.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.contacts.presentation.ContactsParameters
import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.model.CountryCode
import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.home.domain.usecase.GetAvailableCountriesUseCase
import com.topiichat.app.features.home.domain.usecase.GetRecentRemittancesUseCase
import com.topiichat.app.features.remittance.presentation.RemittanceParameters
import com.topiichat.app.features.send_remittance.domain.model.FxRateDomain
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.domain.usecases.CreateRemittanceIntentionUseCase
import com.topiichat.app.features.send_remittance.domain.usecases.GetCardsUseCase
import com.topiichat.app.features.send_remittance.domain.usecases.GetFxRateUseCase
import com.topiichat.app.features.send_remittance.domain.usecases.GetRemittancePurposesUseCase
import com.topiichat.app.features.send_remittance.domain.usecases.SendRemittanceUseCase
import com.topiichat.app.features.send_remittance.presentation.mapper.RecentUsersUiMapper
import com.topiichat.app.features.send_remittance.presentation.model.BtnSendEnablingUi
import com.topiichat.app.features.send_remittance.presentation.model.RecentUserUiModel
import com.topiichat.app.features.send_remittance.presentation.model.RecentUsersUiModel
import com.topiichat.app.features.send_remittance.presentation.model.RecipientUiModel
import com.topiichat.app.features.send_remittance.presentation.model.SendRemittanceUIModel
import com.topiichat.app.features.send_remittance.presentation.model.changeCheckedStatus
import com.topiichat.core.domain.ResultData
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router
import timber.log.Timber

class SendRemittanceViewModel @AssistedInject constructor(
    @Assisted private val parameters: SendRemittanceParameters,
    private val getFxRate: GetFxRateUseCase,
    private val getRemittancePurposes: GetRemittancePurposesUseCase,
    private val getAvailableCountries: GetAvailableCountriesUseCase,
    private val getRecentRemittances: GetRecentRemittancesUseCase,
    private val getCards: GetCardsUseCase,
    private val createRemittanceIntention: CreateRemittanceIntentionUseCase,
    private val sendRemittance: SendRemittanceUseCase,
    private val recentUsersUiMapper: RecentUsersUiMapper,
    appRouter: Router
) : BaseViewModel(appRouter), ISendRemittanceViewModel {

    private val _content: MutableLiveData<SendRemittanceUIModel> = MutableLiveData()
    val content: LiveData<SendRemittanceUIModel> = _content

    private val _showReceiverSumLoader: MutableLiveData<Boolean> = MutableLiveData()
    val showReceiverSumLoader: LiveData<Boolean> = _showReceiverSumLoader

    private val _sendingSum: MutableLiveData<Double> = MutableLiveData()
    val sendingSum: LiveData<Double> = _sendingSum

    private val _receiverSum: MutableLiveData<Double> = MutableLiveData()
    val receiverSum: LiveData<Double> = _receiverSum

    private val _zeroTotalSumSum: MutableLiveData<String> = MutableLiveData()
    val zeroTotalAmount: LiveData<String> = _zeroTotalSumSum

    private var _receiverCountry = MutableLiveData<CountryDomain>()
    val receiverCountry: LiveData<CountryDomain> = _receiverCountry

    private var _fxRate = MutableLiveData<FxRateDomain>()
    val fxRate: LiveData<FxRateDomain> = _fxRate

    private var _remittancePurposes = MutableLiveData<List<RemittancePurposeDomain>>()
    val remittancePurposes = _remittancePurposes

    private var _recentUsers = MutableLiveData<RecentUsersUiModel>()
    val recentUsers = _recentUsers

    private val _btnSendEnabling: MutableLiveData<BtnSendEnablingUi> = MutableLiveData()
    val btnSendEnabling: LiveData<BtnSendEnablingUi> = _btnSendEnabling

    private var _recipientModel = MutableLiveData<RecipientUiModel>()
    val recipientModel = _recipientModel

    private val availableCountries = MutableLiveData<AvailableCountriesDomain>()
    private val currentFxRate = MutableLiveData<FxRateDomain>()

    private var sendAmount = 0.0
    private var receiverAmount = 0.0
    private var fxRateId = ""

    private var recipientId: String = ""
    private var purposeCode: String = ""
    private var cardToken: String = ""
    private var description: String = ""
    private var isCheckedSwitch = false

    private var fxRateJob: Job? = null

    init {
        _btnSendEnabling.value = BtnSendEnablingUi(
            isEnabled = false,
            backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
        )

        viewModelScope.launch {
            _showLoader.value = true
            when (val result = getAvailableCountries()) {
                is ResultData.Success -> {
                    result.data.let {
                        availableCountries.value = it
                        _receiverCountry.value =
                            result.data.countries.first { it.countryCode == CountryCode.GT } //TODO Remove 'fix'
                    }
                    loadRemittancePurposes()
                    loadRecentUsers()
                    loadUserCard()
                    getFxRateForAmount(parameters.country.limitMin)
                }
                is ResultData.Fail -> {
                    _showLoader.value = false
                    handleError(result.error)
                }
            }

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

    override suspend fun loadUserCard() {
        when (val cardsResult = getCards()) {
            is ResultData.Success -> {
                cardToken = cardsResult.data.firstOrNull()?.token ?: ""
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
        onUpdateBtnSend()
    }

    override fun onToCurrencyChanged(countryDomain: CountryDomain) {
        _receiverCountry.value = countryDomain
        getFxRateForAmount(sendAmount, updateOnlyReceiverSum = true)
    }

    override fun getFxRateForAmount(amount: Double, updateOnlyReceiverSum: Boolean) {
        fxRateJob?.cancel()
        _showReceiverSumLoader.value = false
        fxRateJob = viewModelScope.launch {
            _showReceiverSumLoader.value = true
            delay(500)
            val result = getFxRate(
                GetFxRateUseCase.Params(
                    toCountryCode = receiverCountry.value?.code ?: "",
                    toCurrencyCode = receiverCountry.value?.currencyCode ?: "",
                    fromCurrencyCode = parameters.country.currencyCode,
                    sendAmount = amount
                )
            )
            when (result) {
                is ResultData.Success -> {
                    onSuccessFxRate(result.data, updateOnlyReceiverSum)
                }
                is ResultData.Fail -> {
                    handleError(result.error)
                }
            }
            _showReceiverSumLoader.value = false
            _showLoader.value = false
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            com.topiichat.core.R.id.image_view_back -> onClickBack()
            R.id.btn_send -> onSendRemittanceClick()
        }
    }

    override fun onSendRemittanceClick() {
        with(parameters.country) {
            if (sendAmount < limitMin) {
                _showMsgError.postValue("Sending amount can't be less than $currencyCode $limitMin")
                return
            }
            if (sendAmount >= parameters.country.limitMax) {
                _showMsgError.postValue("Sending amount can't be more than $currencyCode $limitMax")
                return
            }

            viewModelScope.launch {
                _showLoader.value = true
                when (val result = createRemittanceIntention(
                    CreateRemittanceIntentionUseCase.Params(
                        recipientId,
                        fxRate.value
                    )
                )) {
                    is ResultData.Success -> {
                        sendRemittance()
                    }
                    is ResultData.Fail -> {
                        _showLoader.value = false
                        handleError(result.error)
                    }
                }
            }
        }
    }

    override fun sendRemittance() {
        viewModelScope.launch {
            when (val result = sendRemittance(
                SendRemittanceUseCase.Params(
                    recipientId = recipientId,
                    fxRateId = fxRateId,
                    description = description,
                    purposeCode = purposeCode,
                    cardTokenized = cardToken
                )
            )) {
                is ResultData.Success -> {
                    navigate(
                        MainScreens.RemittanceDetail(
                            RemittanceParameters(
                                remittanceId = result.data.id,
                                countryFlag = receiverCountry.value?.flagImageUrl ?: "",
                                toCountryName = receiverCountry.value?.name ?: ""
                            )
                        )
                    )
                }
                is ResultData.Fail -> {
                    navigate(MainScreens.RemittanceError)
                }
            }
            _showLoader.value = false
        }
    }

    override fun onAddRecipientClick() {
        navigate(MainScreens.Contacts(ContactsParameters(isSingleSelection = true)))
    }

    override fun onAmountChanged(amount: Double) {
        if (amount <= 0.0) {
            _receiverSum.postValue(0.0)
            _zeroTotalSumSum.postValue(parameters.country.currencyCode)
        }
        getFxRateForAmount(amount, updateOnlyReceiverSum = true)
    }

    override fun onSuccessFxRate(fxRate: FxRateDomain?, updateOnlyReceiverSum: Boolean) {
        fxRate?.let {
            currentFxRate.value = it
            sendAmount = it.sendingAmount
            receiverAmount = it.receivingAmount
            fxRateId = it.fxSpotId
            _fxRate.postValue(it)
        }
        if (updateOnlyReceiverSum) {
            _receiverSum.postValue(receiverAmount)
        } else {
            _content.value = SendRemittanceUIModel(
                fxRate = fxRate,
                availableCountries = requireNotNull(availableCountries.value),
                currentCountry = parameters.country
            )
        }
        onUpdateBtnSend()
    }

    override fun onRecentUserClick(recentUser: RecentUserUiModel) {
        val newModel = recentUsers.value?.changeCheckedStatus(recentUser)
        newModel?.let { _recentUsers.value = it }
        recipientId = recentUser.data.recipientId

        val recipientCountry = availableCountries.value
            ?.countries
            ?.firstOrNull { "+${it.dialCountryCode}" == recentUser.data.dialCode }
        val recipientUiModel = RecipientUiModel(
            userData = recentUser.data,
            recipientCountry = recipientCountry
        )
        _recipientModel.value = recipientUiModel

        onUpdateBtnSend()
    }

    override fun onAddUserClick() {
        navigate(MainScreens.Contacts(ContactsParameters(isSingleSelection = true)))
    }

    override fun onUpdateBtnSend() {
        Timber.d("isCheckedSwitch $isCheckedSwitch")
        Timber.d("recipientId $recipientId")
        Timber.d("cardId $cardToken")
        Timber.d("purposeCode $purposeCode")
        Timber.d("fxRateId $fxRateId")

        val btnSendEnabling = if (isCheckedSwitch &&
            recipientId.isNotEmpty() &&
            cardToken.isNotEmpty() &&
            purposeCode.isNotEmpty() &&
            fxRateId.isNotEmpty()
        ) {
            BtnSendEnablingUi(
                isEnabled = true,
                backgroundId = com.topiichat.core.R.drawable.bg_button
            )
        } else {
            BtnSendEnablingUi(
                isEnabled = false,
                backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
            )
        }
        _btnSendEnabling.value = btnSendEnabling
    }

    override fun onPurposeNotSelected() {
        purposeCode = ""
        onUpdateBtnSend()
    }

    override fun onPurposeSelected(position: Int) {
        purposeCode = remittancePurposes.value?.get(position)?.value ?: ""
        onUpdateBtnSend()
    }

    override fun onCheckedChanged(id: Int?, isChecked: Boolean) {
        if (id == null) return
        when (id) {
            R.id.switch_payment -> {
                isCheckedSwitch = isChecked
            }
        }
        onUpdateBtnSend()
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted parameters: SendRemittanceParameters
        ): SendRemittanceViewModel
    }
}