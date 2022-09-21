package com.topiichat.app.features.kyc.email.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.extension.isValidEmail
import com.topiichat.app.core.extension.toIso3CountryCode
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.chats.ChatsScreens
import com.topiichat.app.features.kyc.base.domain.model.toRegisterParameters
import com.topiichat.app.features.kyc.base.domain.usecases.GetTokenAliceUseCase
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.kyc.email.domain.OnboardingDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class EnterEmailViewModel @AssistedInject constructor(
    @Assisted private val parameters: EnterEmailParameters,
    private val getTokenAlice: GetTokenAliceUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IEnterEmailViewModel {

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

    private val _startOnboarding: MutableLiveData<OnboardingDomain> = MutableLiveData()
    val startOnboarding: LiveData<OnboardingDomain> = _startOnboarding

    private var email: String = ""

    init {
        _btnContinueUiState.value = BtnContinueUiState(
            isEnabled = false,
            backgroundId = R.drawable.bg_button_unenabled
        )
    }

    override fun onEmailChanged(value: String) {
        email = value
        onUpdateContinueBtn()
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
        }
    }

    override fun onUpdateContinueBtn() {
        val uiState = if (email.isNotEmpty() && email.isValidEmail()) {
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
        viewModelScope.launch {
            _showLoader.value = true
            when (val result = getTokenAlice(
                GetTokenAliceUseCase.Params(
                    email = email,
                    firstName = parameters.personalData.firstName,
                    lastName = parameters.personalData.lastName
                )
            )) {
                is ResultData.Success -> {
                    val iso3Code = parameters.personalData.isoCode2.toIso3CountryCode()
                    _startOnboarding.postValue(
                        OnboardingDomain(
                            result.data.token,
                            iso3Code
                        )
                    )
                }
                is ResultData.Fail -> {
                    _showMsgError.postValue(result.error.message)
                }
            }
            _showLoader.value = false
        }
    }

    override fun onClickClose() {
        backTo(ChatsScreens.ChatsList)
    }

    override fun onSuccessVerification(userId: String) {
        parameters.registerModel?.let { kycRegisterModel ->
            navigate(MainScreens.Register(kycRegisterModel.toRegisterParameters(userId)))
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            parameters: EnterEmailParameters
        ): EnterEmailViewModel
    }
}