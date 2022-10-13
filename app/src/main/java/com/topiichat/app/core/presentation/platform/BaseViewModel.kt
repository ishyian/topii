package com.topiichat.app.core.presentation.platform

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.topiichat.app.core.exception.Failure
import com.topiichat.app.core.exception.data.model.NoNetworkConnectionException
import com.topiichat.app.core.exception.data.model.ServiceUnavailableException
import com.topiichat.app.core.exception.data.model.WrongRequestException
import com.topiichat.app.core.exception.domain.ErrorDomain
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.error.presentation.ErrorParameters
import com.topiichat.app.features.error.presentation.model.Error
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.Screen

/**
 * Base ViewModel class with default Failure handling.
 * @see ViewModel
 * @see Failure
 */
abstract class BaseViewModel(private val router: Router) : ViewModel(), IBaseViewModel {

    abstract fun onClick(view: View?)

    protected val _showLoader: MutableLiveData<Boolean> = MutableLiveData()
    val showLoader: LiveData<Boolean> = _showLoader

    protected val _showMsgError: SingleLiveData<String> = SingleLiveData()
    val showMsgError: LiveData<String> = _showMsgError

    protected fun navigate(screen: Screen, clearBackStack: Boolean = false) {
        if (clearBackStack) router.newRootScreen(screen)
        else router.navigateTo(screen)
    }

    protected fun backTo(screen: Screen) {
        router.backTo(screen)
    }

    protected fun handleError(error: ErrorDomain) {
        when (error.exceptionClass) {
            NoNetworkConnectionException::class.java -> showErrorScreen(Error.NO_NETWORK)
            WrongRequestException::class.java -> showErrorScreen(Error.WRONG_REQUEST)
            ServiceUnavailableException::class.java -> showErrorScreen(Error.UNKNOWN)
            else -> _showMsgError.postValue(error.message)
        }
    }

    private fun showErrorScreen(error: Error) {
        navigate(MainScreens.Error(ErrorParameters(error)))
    }

    override fun onClickBack() {
        router.exit()
    }

    override fun onClickClose() {
        onClickBack()
    }
}