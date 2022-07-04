package com.topiichat.app.core.presentation.platform

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.topiichat.app.core.exception.Failure
import com.topiichat.app.core.presentation.navigation.Navigator

/**
 * Base ViewModel class with default Failure handling.
 * @see ViewModel
 * @see Failure
 */
abstract class BaseViewModel : ViewModel(), IBaseViewModel {

    abstract fun onClick(view: View?)

    protected val _navigate: SingleLiveData<Navigator> = SingleLiveData()
    val navigate: LiveData<Navigator> = _navigate

    protected val _showLoader: MutableLiveData<Boolean> = MutableLiveData()
    val showLoader: LiveData<Boolean> = _showLoader

    protected val _showMsgError: SingleLiveData<String> = SingleLiveData()
    val showMsgError: LiveData<String> = _showMsgError

    protected fun onNetworkError() {
        _showMsgError.setValue("Something wrong with your network connection")
    }

    override fun onClickBack() {
        _navigate.setValue(
            Navigator(
                isBack = true
            )
        )
    }

    override fun onClickClose() {
        onClickBack()
    }
}