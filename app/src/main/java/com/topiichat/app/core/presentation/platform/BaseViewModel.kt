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

    protected val _showMsgError: MutableLiveData<String> = MutableLiveData()
    val showMsgError: LiveData<String> = _showMsgError

    private val _failure: MutableLiveData<Failure> = MutableLiveData()
    val failure: LiveData<Failure> = _failure

    protected fun handleFailure(failure: Failure) {
        _failure.value = failure
    }

    override fun onClickBack() {
        _navigate.setValue(Navigator(
            isBack = true
        ))
    }

    override fun onClickClose() {
        onClickBack()
    }
}