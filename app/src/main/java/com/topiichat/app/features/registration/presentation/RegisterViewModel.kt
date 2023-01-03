package com.topiichat.app.features.registration.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.base.domain.model.KYCRegisterDomain
import com.topiichat.app.features.kyc.base.domain.usecases.GetKYCStatusUseCase
import com.topiichat.app.features.kyc.personal_data.presentation.PersonalDataParameters
import com.topiichat.app.features.personal_information.presentation.PersonalInfoParameters
import com.topiichat.app.features.registration.domain.model.ProfileDomain
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.app.features.registration.domain.usecases.RegisterUseCase
import com.topiichat.app.features.registration.domain.usecases.SaveAuthDataUseCase
import com.topiichat.app.features.registration.presentation.model.BtnRegisterEnablingUi
import com.topiichat.core.domain.ResultData
import com.topiichat.core.exception.domain.ErrorDomain
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class RegisterViewModel @AssistedInject constructor(
    @Assisted("registerParameters") private val parameters: RegisterParameters,
    private val register: RegisterUseCase,
    private val getKYCStatus: GetKYCStatusUseCase,
    private val saveAuthData: SaveAuthDataUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IRegisterViewModel {

    private val _btnRegisterEnabling: MutableLiveData<BtnRegisterEnablingUi> = MutableLiveData()
    val btnRegisterEnabling: LiveData<BtnRegisterEnablingUi> = _btnRegisterEnabling

    private var isCheckedSwitch1 = false
    private var isCheckedSwitch2 = false

    init {
        _btnRegisterEnabling.value = BtnRegisterEnablingUi(
            isEnabled = false,
            backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
        )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            com.topiichat.core.R.id.image_view_back -> {
                onClickBack()
            }
            com.topiichat.core.R.id.image_view_close -> {
                onClickClose()
            }
            R.id.btn_register -> {
                if (parameters.isFromAuth) {
                    onRegister()
                } else {
                    onRegisterKYC()
                }
            }
        }
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
                backgroundId = com.topiichat.core.R.drawable.bg_button
            )
        } else {
            BtnRegisterEnablingUi(
                isEnabled = false,
                backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
            )
        }
        _btnRegisterEnabling.value = btnRegisterEnabling
    }

    override fun onRegister() {
        viewModelScope.launch {
            val request = RegisterUseCase.Params(
                phoneNumber = parameters.phoneNumber,
                code = parameters.code,
                authyId = parameters.authyId,
                pinCode = parameters.pinCode
            )
            _showLoader.value = true
            val result = register(request)
            onRenderRegister(result)
        }
    }

    override fun onRenderRegister(result: ResultData<RegisterDomain>) {
        when (result) {
            is ResultData.Success -> {
                onSuccessRegister(result.data.accessToken, result.data.senderId, result.data.profile)
            }
            is ResultData.Fail -> onFailRegister(result.error)
        }
    }

    override fun onSuccessRegister(accessToken: String, senderId: String, profile: ProfileDomain) {
        viewModelScope.launch {
            /* when (val kycStatusResult = getKYCStatus(GetKYCStatusUseCase.Params(accessToken))) {
                 is ResultData.Success -> {
                     if (kycStatusResult.data == KYCStatus.KYC_NOT_VERIFIED) {
                         onKYCStatusNotVerified()
                     } else {
                         saveAuthData(SaveAuthDataUseCase.Params(accessToken, senderId, parameters.isoCode))
                         navigate(MainScreens.Home, true)
                     }
                 }
                 is ResultData.Fail -> {
                     _showMsgError.postValue(kycStatusResult.error.message)
                 }
             }*/
            saveAuthData(SaveAuthDataUseCase.Params(accessToken, senderId, parameters.isoCode))
            navigate(MainScreens.PersonalInformation(PersonalInfoParameters(profile)))
            _showLoader.value = false
        }
    }

    override fun onFailRegister(error: ErrorDomain) {
        handleError(error)
        _showLoader.value = false
    }

    override fun onKYCStatusNotVerified() = with(parameters) {
        val personalDataParameters = PersonalDataParameters(
            isoCode2 = isoCode,
            registerModel = KYCRegisterDomain(
                phoneNumber = phoneNumber,
                authyId = authyId,
                code = code,
                pinCode = pinCode,
                isoCode2 = isoCode
            )
        )
        navigate(KYCScreens.PersonalData(personalDataParameters))
    }

    override fun onRegisterKYC() {
        viewModelScope.launch {
            val request = RegisterUseCase.Params(
                phoneNumber = parameters.phoneNumber,
                code = parameters.code,
                authyId = parameters.authyId,
                pinCode = parameters.pinCode,
                aliceUserId = parameters.aliceUserId
            )
            _showLoader.value = true
            val result = register(request)
            onRenderKYCRegister(result)
        }
    }

    override fun onRenderKYCRegister(result: ResultData<RegisterDomain>) {
        when (result) {
            is ResultData.Success -> {
                onSuccessKYCRegister(result.data.accessToken, result.data.senderId)
            }
            is ResultData.Fail -> onFailRegister(result.error)
        }
    }

    override fun onSuccessKYCRegister(accessToken: String, senderId: String) {
        viewModelScope.launch {
            saveAuthData(SaveAuthDataUseCase.Params(accessToken, senderId, parameters.isoCode))
            navigate(MainScreens.Home, true)
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted("registerParameters") parameters: RegisterParameters
        ): RegisterViewModel
    }
}