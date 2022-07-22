package com.topiichat.app.features.registration.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.app.features.registration.domain.usecases.RegisterUseCase
import com.topiichat.app.features.registration.presentation.model.BtnRegisterEnablingUi
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class RegisterViewModel @AssistedInject constructor(
    @Assisted("registerParameters") private val parameters: RegisterParameters,
    private val registerUseCase: RegisterUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IRegisterViewModel {

    private val _btnRegisterEnabling: MutableLiveData<BtnRegisterEnablingUi> = MutableLiveData()
    val btnRegisterEnabling: LiveData<BtnRegisterEnablingUi> = _btnRegisterEnabling

    private var isCheckedSwitch1 = false
    private var isCheckedSwitch2 = false

    init {
        _btnRegisterEnabling.value = BtnRegisterEnablingUi(
            isEnabled = false,
            backgroundId = R.drawable.bg_button_unenabled
        )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
            }
            R.id.btn_register -> {
                if (parameters.isFromAuth) {
                    onRegisterRequest()
                } else {
                    onRegisterKYC()
                }
            }
        }
    }

    private fun onRegisterKYC() {
        viewModelScope.launch {
            _showLoader.value = true
            delay(1000)
            onSuccessRegisterKYC()
            _showLoader.value = false
        }
    }

    private fun onSuccessRegisterKYC() {
        navigate(MainScreens.Chats, true)
    }

    override fun onCheckedChanged(id: Int?, isChecked: Boolean) {
        if (id == null) return
        when (id) {
            R.id.switch1 -> {
                isCheckedSwitch1 = isChecked
            }
            R.id.switch2 -> {
                isCheckedSwitch2 = isChecked
            }
        }
        onUpdateBtnRegister()
    }

    override fun onUpdateBtnRegister() {
        val btnRegisterEnabling = if (isCheckedSwitch1 && isCheckedSwitch2) {
            BtnRegisterEnablingUi(
                isEnabled = true,
                backgroundId = R.drawable.bg_button
            )
        } else {
            BtnRegisterEnablingUi(
                isEnabled = false,
                backgroundId = R.drawable.bg_button_unenabled
            )
        }
        _btnRegisterEnabling.value = btnRegisterEnabling
    }

    override fun onRegisterRequest() {
        viewModelScope.launch {
            val request = RegisterUseCase.Params(
                phoneNumber = parameters.phoneNumber,
                code = parameters.code,
                authyId = parameters.authyId,
                pinCode = parameters.pinCode
            )
            _showLoader.value = true
            delay(1000)
            val result = registerUseCase(request)
            onRenderRegister(result)
            _showLoader.value = false
        }
    }

    override fun onRenderRegister(result: ResultData<RegisterDomain>) {
        when (result) {
            is ResultData.Success -> {
                result.data?.let {
                    onSuccessRegister()
                }
            }
            is ResultData.Fail -> onFailRegister()
            is ResultData.NetworkError -> onNetworkError()

        }
    }

    override fun onSuccessRegister() {
        //todo("parameters")
        navigate(MainScreens.Chats, true)
    }

    override fun onFailRegister() {
        //todo("parameters")
        _showMsgError.setValue("Fail")
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted("registerParameters") parameters: RegisterParameters
        ): RegisterViewModel
    }
}