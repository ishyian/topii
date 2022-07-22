package com.topiichat.app.features.kyc.email.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class EnterEmailViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IEnterEmailViewModel {

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

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
        val uiState = if (email.isNotEmpty()) {
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
        navigate(KYCScreens.Document)
    }

    override fun onClickClose() {
        backTo(MainScreens.Chats)
    }
}