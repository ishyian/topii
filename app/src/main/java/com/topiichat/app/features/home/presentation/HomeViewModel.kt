package com.topiichat.app.features.home.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.chats.ChatsScreens
import com.topiichat.app.features.home.domain.model.CurrentCountryDomain
import com.topiichat.app.features.home.domain.model.RemittanceDomain
import com.topiichat.app.features.home.domain.usecase.GetCurrentCountryAvailabilityUseCase
import com.topiichat.app.features.home.domain.usecase.GetRemittanceHistoryUseCase
import com.topiichat.app.features.home.presentation.mapper.HomeRemittanceUiMapper
import com.topiichat.app.features.home.presentation.model.HomeRemittanceHistoryUiModel
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.base.domain.model.KYCStatus
import com.topiichat.app.features.kyc.base.domain.usecases.GetKYCStatusUseCase
import com.topiichat.app.features.kyc.personal_data.presentation.PersonalDataParameters
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.request_remittance.presentation.RequestRemittanceParameters
import com.topiichat.app.features.send_remittance.presentation.SendRemittanceParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getRemittanceHistory: GetRemittanceHistoryUseCase,
    private val getCurrentCountryAvailability: GetCurrentCountryAvailabilityUseCase,
    private val getKYCStatus: GetKYCStatusUseCase,
    private val getAuthData: GetAuthDataUseCase,
    private val remittanceUiMapper: HomeRemittanceUiMapper,
    appRouter: Router
) : BaseViewModel(appRouter), IHomeViewModel {

    private val _content: MutableLiveData<HomeRemittanceHistoryUiModel> = MutableLiveData()
    val content: LiveData<HomeRemittanceHistoryUiModel> = _content

    private val _availableCountryFeatures: MutableLiveData<CurrentCountryDomain> = MutableLiveData()
    val availableCountryFeatures: LiveData<CurrentCountryDomain> = _availableCountryFeatures

    private val _showTotalSumByMonthLoader: MutableLiveData<Boolean> = MutableLiveData()
    val showTotalSumByMonthLoader: LiveData<Boolean> = _showTotalSumByMonthLoader

    private val kycStatus: MutableLiveData<KYCStatus> = MutableLiveData()

    private var month = LocalDateTime.now().monthValue
    private val year by lazy { LocalDateTime.now().year }

    init {
        checkAvailableCountryFeatures()
        loadRemmittanceHistory(month)
    }

    override fun checkAvailableCountryFeatures() {
        viewModelScope.launch {
            _showLoader.value = true
            when (val result = getCurrentCountryAvailability()) {
                is ResultData.Success -> {
                    _availableCountryFeatures.postValue(result.data)
                }
                else -> {
                    //Nothing
                }
            }
            when (val result = getKYCStatus(GetKYCStatusUseCase.Params())) {
                is ResultData.Success -> {
                    kycStatus.postValue(result.data)
                }
                else -> {
                    //Nothing
                }
            }
            _showLoader.value = false
        }
    }

    override fun loadRemmittanceHistory(month: Int) {
        viewModelScope.launch {
            _showTotalSumByMonthLoader.value = true
            val params = GetRemittanceHistoryUseCase.Params(
                year = year,
                month = month
            )
            when (val result = getRemittanceHistory(params)) {
                is ResultData.Success -> {
                    _content.postValue(remittanceUiMapper.map(result.data))
                }
                is ResultData.Fail -> {
                    onFailRemmitanceHistory(result)
                }
            }
            _showTotalSumByMonthLoader.value = false
        }
    }

    override fun onFailRemmitanceHistory(failure: ResultData.Fail) {
        _showMsgError.postValue(failure.error.message)
    }

    override fun onClick(view: View?) {

    }

    override fun onFiltersClick() {

    }

    override fun onTransactionClick(remittanceDomain: RemittanceDomain) {

    }

    override fun onSendPaymentClick() {
        if (kycStatus.value == KYCStatus.KYC_NOT_VERIFIED) {
            viewModelScope.launch {
                val parameters = PersonalDataParameters(getAuthData().isoCode)
                navigate(KYCScreens.PersonalData(parameters))
            }
        } else {
            availableCountryFeatures.value?.countryInfo?.let { countryDomain ->
                navigate(
                    MainScreens.SendRemittance(
                        parameters = SendRemittanceParameters(
                            countryDomain
                        )
                    )
                )
            }
        }
    }

    override fun onChatsClick() {
        navigate(ChatsScreens.ChatsList)
    }

    override fun onRequestPaymentClick() {
        availableCountryFeatures.value?.countryInfo?.let { countryDomain ->
            navigate(
                MainScreens.RequestRemittance(
                    parameters = RequestRemittanceParameters(
                        countryDomain
                    )
                )
            )
        }
    }
}