package com.topiichat.app.features.wallet.bank_account.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.registration.domain.usecases.GetAuthDataUseCase
import com.topiichat.app.features.wallet.WalletScreens
import com.topiichat.core.presentation.platform.BaseViewModel
import com.topiichat.core.presentation.platform.SingleLiveData
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class AddBankAccountViewModel @AssistedInject constructor(
    private val getAuthData: GetAuthDataUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IAddBankAccountViewModel {

    private val _hideKeyboard: MutableLiveData<Unit> = MutableLiveData()
    val hideKeyboard: LiveData<Unit> = _hideKeyboard

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

    private val _setupIbanInputMask: MutableLiveData<String> = MutableLiveData()
    val setupIbanInputMask: LiveData<String> = _setupIbanInputMask

    val showBankDialog: SingleLiveData<List<String>> = SingleLiveData()

    private var bankName = ""
    private var iban = ""
    private var bicCode = ""

    init {
        _btnContinueUiState.value = BtnContinueUiState(
            isEnabled = false,
            backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
        )

        viewModelScope.launch {
            val authData = getAuthData()
            val ibanMask = "${authData.isoCode}[00] [0000] [0000] [0000] [0000] [00]"
            _setupIbanInputMask.postValue(ibanMask)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            com.topiichat.core.R.id.image_view_back -> {
                onClickBack()
            }
            R.id.layout_bank -> {
                onBankClick()
            }
            R.id.btn_continue -> {
                onBtnOkClick()
            }
        }
    }

    override fun onBtnOkClick() {
        navigate(WalletScreens.CardAdded)
    }

    override fun onBankClick() {
        showBankDialog.postValue(listOf("Bank 1", "Bank 2", "Bank 3"))
    }

    override fun onIbanChanged(iban: String) {
        this.iban = iban
        onUpdateContinueBtn()
    }

    override fun onBicChanged(bicCode: String) {
        this.bicCode = bicCode
        onUpdateContinueBtn()
    }

    override fun onBankChanged(bankName: String) {
        this.bankName = bankName
        onUpdateContinueBtn()
    }

    override fun onUpdateContinueBtn() {
        val uiState = if (iban.isNotEmpty() &&
            bicCode.isNotEmpty() &&
            bankName.isNotEmpty()
        ) {
            BtnContinueUiState(
                isEnabled = true,
                backgroundId = com.topiichat.core.R.drawable.bg_button
            )
        } else {
            BtnContinueUiState(
                isEnabled = false,
                backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
            )
        }
        _btnContinueUiState.postValue(uiState)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(): AddBankAccountViewModel
    }
}