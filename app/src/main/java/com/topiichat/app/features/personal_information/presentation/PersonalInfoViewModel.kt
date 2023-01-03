package com.topiichat.app.features.personal_information.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.kyc.personal_data.presentation.model.PersonalData
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class PersonalInfoViewModel @AssistedInject constructor(
    @Assisted private val parameters: PersonalInfoParameters,
    appRouter: Router
) : BaseViewModel(appRouter), IPersonalInfoViewModel {

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

    private var name: String = ""
    private var secondName: String = ""
    private var surname: String = ""
    private var secondSurname: String = ""

    init {
        _btnContinueUiState.value = BtnContinueUiState(
            isEnabled = false,
            backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
        )

        with(parameters.profileDomain) {
            name = firstName
            secondSurname = lastNameSecond
            surname = lastName
            secondName = firstNameSecond
        }

        onUpdateContinueBtn()
    }

    override fun onPersonalDataChanged(type: PersonalData, value: String) {
        when (type) {
            PersonalData.NAME -> {
                name = value
            }
            PersonalData.LAST_NAME -> {
                surname = value
            }

            PersonalData.SECOND_NAME -> {
                secondName = value
            }
            PersonalData.SECOND_LAST_NAME -> {
                secondSurname = value
            }
        }
        onUpdateContinueBtn()
    }

    override fun onUpdateContinueBtn() {
        val isNameValid = name.isNotEmpty()
        val isLastNameValid = surname.isNotEmpty()
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
            com.topiichat.core.R.id.image_view_back -> onClickBack()
            R.id.btn_continue -> onContinueBtnClick()
        }
    }

    override fun onContinueBtnClick() {
        navigate(MainScreens.Home, true)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted parameters: PersonalInfoParameters
        ): PersonalInfoViewModel
    }
}