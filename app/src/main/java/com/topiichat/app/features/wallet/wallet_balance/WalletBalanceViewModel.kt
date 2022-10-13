package com.topiichat.app.features.wallet.wallet_balance

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.home.domain.model.CurrentCountryDomain
import com.topiichat.app.features.home.domain.usecase.GetCurrentCountryAvailabilityUseCase
import com.topiichat.app.features.wallet.WalletScreens
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class WalletBalanceViewModel @AssistedInject constructor(
    private val getCurrentCountryAvailability: GetCurrentCountryAvailabilityUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IWalletBalanceViewModel {

    private val _showBalanceImage: MutableLiveData<Int> = MutableLiveData()
    val showBalanceImage: LiveData<Int> = _showBalanceImage

    private val _showBalance: MutableLiveData<String> = MutableLiveData()
    val showBalance: LiveData<String> = _showBalance

    private val _availableCountryFeatures: MutableLiveData<CurrentCountryDomain> = MutableLiveData()
    val availableCountryFeatures: LiveData<CurrentCountryDomain> = _availableCountryFeatures

    private val _balanceCheck: MutableLiveData<Boolean> = MutableLiveData()

    init {
        checkAvailableCountryFeatures()
        _balanceCheck.value = true
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
            _showLoader.value = false
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_show_balance -> onShowBalanceClick()
            R.id.image_view_back -> onClickBack()
        }
    }

    override fun onAddCreditCardClick() {
        navigate(WalletScreens.AddCard)
    }

    override fun onShowBalanceClick() {
        val balanceCheck = _balanceCheck.value ?: return
        if (balanceCheck.not()) {
            _balanceCheck.value = true
            _showBalance.value = "---"
            _showBalanceImage.value = R.drawable.ic_password_not
        } else {
            _balanceCheck.value = false
            _showBalance.value = "1200.50 USD"
            _showBalanceImage.value = R.drawable.ic_password_yes
        }
    }

    override fun navigateToAddBankAccount() {
        navigate(WalletScreens.AddBankAccount)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): WalletBalanceViewModel
    }
}