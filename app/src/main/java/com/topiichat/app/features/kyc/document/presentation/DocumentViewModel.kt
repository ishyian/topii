package com.topiichat.app.features.kyc.document.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class DocumentViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IDocumentViewModel {

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

    private var email: String = ""

    init {
        _btnContinueUiState.value = BtnContinueUiState(
            isEnabled = false,
            backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
        )
    }

    override fun onDocumentChanged(value: String) {
        email = value
        onUpdateContinueBtn()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_continue -> {
                onContinueClick()
            }
            com.topiichat.core.R.id.image_view_back -> {
                onClickBack()
            }
            com.topiichat.core.R.id.image_view_close -> {
                onClickClose()
            }
        }
    }

    override fun onUpdateContinueBtn() {
        val uiState = if (email.isNotEmpty()) {
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

    override fun onContinueClick() {
        navigate(KYCScreens.BirthDate)
    }
}