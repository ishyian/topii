package com.topiichat.app.features.kyc.personal_data.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.kyc.email.presentation.EnterEmailParameters
import com.topiichat.app.features.kyc.personal_data.domain.PersonalDataDomain
import com.topiichat.app.features.kyc.personal_data.presentation.model.PersonalData
import com.topiichat.chat.ChatsScreens
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class PersonalDataViewModel @AssistedInject constructor(
    @Assisted private val parameters: PersonalDataParameters,
    appRouter: Router
) : BaseViewModel(appRouter), IPersonalDataViewModel {

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

    private var name: String = ""
    private var lastName: String = ""
    private var secondLastName: String = ""

    init {
        _btnContinueUiState.value = BtnContinueUiState(
            isEnabled = false,
            backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
        )
    }

    override fun onPersonalDataChanged(type: PersonalData, value: String) {
        when (type) {
            PersonalData.NAME -> {
                name = value
            }
            PersonalData.LAST_NAME -> {
                lastName = value
            }

            PersonalData.SECOND_NAME -> {
                secondLastName = value
            }
        }
        onUpdateContinueBtn()
    }

    override fun onUpdateContinueBtn() {
        val isNameValid = name.isNotEmpty()
        val isLastNameValid = lastName.isNotEmpty()
        val uiState = if (isNameValid && isLastNameValid) {
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

    override fun onContinueClick() = with(parameters) {
        val parameters = EnterEmailParameters(
            PersonalDataDomain(
                firstName = name,
                lastName = lastName,
                isoCode2 = isoCode2,
                secondLastName = secondLastName
            ),
            registerModel = registerModel
        )
        navigate(KYCScreens.EnterEmail(parameters))
    }

    override fun onClickClose() {
        backTo(ChatsScreens.ChatsList)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            parameters: PersonalDataParameters
        ): PersonalDataViewModel
    }
}