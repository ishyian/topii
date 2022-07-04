package com.topiichat.app.features.terms.presentation

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.constants.Constants
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseViewModel

class TermsViewModel : BaseViewModel(), ITermsViewModel {

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
        _navigate.setValue(Navigator(R.id.action_terms_to_permission))
    }
}