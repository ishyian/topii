package com.topiichat.app.features.remittance.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.topiichat.app.R
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.remittance.domain.usecases.GetRemittanceDetailUseCase
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.launch
import ru.terrakok.cicerone.Router

class RemittanceDetailViewModel @AssistedInject constructor(
    @Assisted private val parameters: RemittanceParameters,
    private val getRemittanceDetail: GetRemittanceDetailUseCase,
    appRouter: Router
) : BaseViewModel(appRouter), IRemittanceDetailViewModel {

    private val _remittanceDetail: MutableLiveData<RemittanceDomain> = MutableLiveData()
    val remittanceDetail: LiveData<RemittanceDomain> = _remittanceDetail

    init {
        if (parameters.remittance != null) {
            _remittanceDetail.postValue(parameters.remittance)
        } else {
            loadRemittanceDetail()
        }
    }

    override fun loadRemittanceDetail() {
        viewModelScope.launch {
            _showLoader.value = true
            when (val result = getRemittanceDetail(
                GetRemittanceDetailUseCase.Params(
                    parameters.remittanceId
                )
            )) {
                is ResultData.Success -> {
                    _remittanceDetail.postValue(result.data)
                }
                is ResultData.Fail -> {
                    _showMsgError.postValue(result.error.message)
                }
                is ResultData.NetworkError -> onNetworkError()
            }
            _showLoader.value = false
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.image_view_back -> onClickBack()
        }
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(
            @Assisted parameters: RemittanceParameters
        ): RemittanceDetailViewModel
    }
}