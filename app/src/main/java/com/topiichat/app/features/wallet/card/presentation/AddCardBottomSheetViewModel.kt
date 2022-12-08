package com.topiichat.app.features.wallet.card.presentation

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddCardBottomSheetViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IAddCardBottomSheetViewModel {

    private val _dismissDialog: MutableLiveData<Unit> = MutableLiveData()
    val dismissDialog: LiveData<Unit> = _dismissDialog

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_add -> {
                Timber.d("on btn add viewmodel")
                _dismissDialog.value = Unit
            }
        }
    }
}