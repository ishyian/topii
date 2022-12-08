package com.topiichat.core.presentation.error

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.core.R
import com.topiichat.core.presentation.error.model.Error
import com.topiichat.core.presentation.error.model.ErrorUiModel
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.terrakok.cicerone.Router

class ErrorViewModel @AssistedInject constructor(
    @Assisted parameters: ErrorParameters,
    appRouter: Router
) : BaseViewModel(appRouter), IErrorViewModel {

    private val _errorUiModel: MutableLiveData<ErrorUiModel> = MutableLiveData()
    val errorUiModel: LiveData<ErrorUiModel> = _errorUiModel

    init {
        _errorUiModel.value = when (parameters.error) {
            Error.NO_NETWORK -> ErrorUiModel(
                title = R.string.error_no_network_title,
                subtitle = R.string.error_no_network_description,
                buttonText = R.string.error_no_network_button_text,
                icon = R.drawable.ic_error_no_internet
            )
            Error.INSUFFICIENT_BALANCE -> ErrorUiModel(
                title = R.string.error_insufficient_funds_title,
                subtitle = R.string.error_insufficient_funds_description,
                buttonText = R.string.error_insufficient_funds_button_text,
                icon = R.drawable.ic_error_insufficient_funds
            )
            Error.WRONG_REQUEST -> ErrorUiModel(
                title = R.string.error_wrong_request_title,
                subtitle = R.string.error_wrong_request_description,
                buttonText = R.string.error_wrong_request_button_text,
                icon = R.drawable.ic_error_wrong_request
            )
            Error.UNKNOWN -> ErrorUiModel(
                title = R.string.error_unknown_title,
                subtitle = R.string.error_unknown_description,
                buttonText = R.string.error_unknown_button_text,
                icon = R.drawable.ic_error
            )
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_back -> onClickBack()
            R.id.btn_return -> onClickBack()
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted parameters: ErrorParameters
        ): ErrorViewModel
    }
}