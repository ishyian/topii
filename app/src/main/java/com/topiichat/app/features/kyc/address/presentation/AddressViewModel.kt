package com.topiichat.app.features.kyc.address.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.core.presentation.platform.SingleLiveData
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.address.domain.AddressDomain
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IAddressViewModel {

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

    val showAddressDialog: SingleLiveData<List<String>> = SingleLiveData()
    val showCountryDialog: SingleLiveData<List<String>> = SingleLiveData()
    val showRegionDialog: SingleLiveData<List<String>> = SingleLiveData()

    private val addressDomain = AddressDomain()

    init {
        _btnContinueUiState.value = BtnContinueUiState(
            isEnabled = false,
            backgroundId = R.drawable.bg_button_unenabled
        )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_continue -> {
                onContinueClick()
            }
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
            }
            R.id.text_country -> {
                showCountryDialog.postValue(listOf("Ukraine", "Spain", "Portugal"))
            }
            R.id.text_region -> {
                showRegionDialog.postValue(listOf("Region 1", "Region 2"))
            }
            R.id.text_address -> {
                showAddressDialog.postValue(listOf("Address 1", "Address 2", "Address 3"))
            }
        }
    }

    override fun onUpdateContinueBtn() {
        val uiState = if (addressDomain.isValid()) {
            BtnContinueUiState(
                isEnabled = true,
                backgroundId = R.drawable.bg_button
            )
        } else {
            BtnContinueUiState(
                isEnabled = false,
                backgroundId = R.drawable.bg_button_unenabled
            )
        }
        _btnContinueUiState.postValue(uiState)
    }

    override fun onContinueClick() {
        navigate(KYCScreens.IdentityVerification)
    }

    override fun onAddressChanged(value: String) {
        addressDomain.address = value
        onUpdateContinueBtn()
    }

    override fun onCountryChanged(value: String) {
        addressDomain.country = value
        onUpdateContinueBtn()
    }

    override fun onRegionChanged(value: String) {
        addressDomain.region = value
        onUpdateContinueBtn()
    }

    override fun onCityChanged(value: String) {
        addressDomain.city = value
        onUpdateContinueBtn()
    }

    override fun onPostalCodeChanged(value: String) {
        addressDomain.postalCode = value
        onUpdateContinueBtn()
    }
}