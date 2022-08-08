package com.topiichat.app.features.kyc.camera_verification

import android.net.Uri
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.topiichat.app.R
import com.topiichat.app.core.presentation.platform.BaseViewModel
import com.topiichat.app.features.MainScreens
import com.topiichat.app.features.chats.ChatsScreens
import com.topiichat.app.features.registration.presentation.RegisterParameters
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

@HiltViewModel
class IdentityVerificationViewModel @Inject constructor(
    appRouter: Router
) : BaseViewModel(appRouter), IIdentityVerificationViewModel {

    private val _takePhotoFromCamera: MutableLiveData<String> = MutableLiveData()
    val takePhotoFromCamera: LiveData<String> = _takePhotoFromCamera

    private var photoUri: Uri? = null

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_take_picture -> {
                onTakePhotoClick()
            }
            R.id.image_view_back -> {
                onClickBack()
            }
            R.id.image_view_close -> {
                onClickClose()
            }
        }
    }

    private fun onTakePhotoClick() {
        _takePhotoFromCamera.postValue(getNewPhotoFileName())
    }

    override fun onClickClose() {
        backTo(ChatsScreens.ChatsList)
    }

    override fun onCameraImageSelected(uri: Uri?) {
        photoUri = uri
        navigate(MainScreens.Register(RegisterParameters(isFromAuth = false)))
    }

    override fun getNewPhotoFileName(): String {
        return System.currentTimeMillis().toString() + ".jpg"
    }
}