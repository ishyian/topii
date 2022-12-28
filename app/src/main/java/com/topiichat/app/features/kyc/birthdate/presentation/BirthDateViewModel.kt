package com.topiichat.app.features.kyc.birthdate.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.features.kyc.KYCScreens
import com.topiichat.app.features.kyc.base.presentation.model.BtnContinueUiState
import com.topiichat.core.extension.isValidBirthday
import com.topiichat.core.presentation.platform.BaseViewModel
import com.topiichat.core.presentation.platform.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BirthDateViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IBirthDateViewModel {

    private val _btnContinueUiState: MutableLiveData<BtnContinueUiState> = MutableLiveData()
    val btnContinueUiState: LiveData<BtnContinueUiState> = _btnContinueUiState

    val showBirthPlaceDialog: SingleLiveData<List<String>> = SingleLiveData()

    private var birthday: Date? = null
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private var birthDate = ""
    private var birthPlace = ""

    init {
        _btnContinueUiState.value = BtnContinueUiState(
            isEnabled = false,
            backgroundId = com.topiichat.core.R.drawable.bg_button_unenabled
        )
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_continue -> {
                onContinueClick()
            }
            R.id.text_birth_place -> {
                onBirthPlaceClick()
            }
            com.topiichat.core.R.id.image_view_back -> {
                onClickBack()
            }
            com.topiichat.core.R.id.image_view_close -> {
                onClickClose()
            }
        }
    }

    override fun onBirthPlaceClick() {
        showBirthPlaceDialog.postValue(listOf("Ukraine", "Spain", "Portugal"))
    }

    override fun onBirthDateChanged(value: String) {
        birthDate = value
        onUpdateContinueBtn()

        if (value.length < 10) {
            return
        }

        val parsedDate = try {
            sdf.parse(value)
        } catch (e: ParseException) {
            _showMsgError.setValue("Birth date is invalid")
            birthday = null
            return
        }

        if (!parsedDate.isValidBirthday()) {
            _showMsgError.setValue("Birth date is invalid")
            birthday = null
            return
        }
        birthday = parsedDate
    }

    override fun onUpdateContinueBtn() {
        val uiState = if (birthDate.length >= 10 &&
            birthday != null &&
            birthday.isValidBirthday() &&
            birthPlace.isNotEmpty()
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

    override fun onContinueClick() {
        navigate(KYCScreens.Address)
    }

    override fun onBirthPlaceChanged(birthPlace: String) {
        this.birthPlace = birthPlace
    }
}