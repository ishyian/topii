package com.topiichat.app.features.kyc.personal_data.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.app.features.kyc.personal_data.presentation.model.PersonalData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class PersonalDataViewModel @Inject constructor(
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
            backgroundId = R.drawable.bg_button_unenabled
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

    override fun onContinueClick() {
        navigate(KYCScreens.EnterEmail)
    }

    override fun onClickClose() {
        backTo(MainScreens.Chats)
    }
}