package com.topiichat.app.features.terms.presentation

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.features.MainScreens
import com.topiichat.core.constants.Constants
import com.topiichat.core.presentation.platform.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class TermsViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), ITermsViewModel {

    private val _goActionView: MutableLiveData<Uri> = MutableLiveData()
    val goActionView: LiveData<Uri> = _goActionView

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.terms_url -> {
                onClickTermsUrl()
            }
            R.id.next_screen -> {
                onClickNextScreen()
            }
        }
    }

    override fun onClickTermsUrl() {
        _goActionView.value = Uri.parse(Constants.URL_TERMS)
    }

    override fun onClickNextScreen() {
        navigate(MainScreens.Permission)
    }
}