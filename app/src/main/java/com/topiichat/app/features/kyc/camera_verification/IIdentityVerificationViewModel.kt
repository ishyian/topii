package com.topiichat.app.features.kyc.camera_verification

import android.net.Uri
import com.topiichat.app.core.presentation.platform.IBaseViewModel

interface IIdentityVerificationViewModel : IBaseViewModel {
    fun onCameraImageSelected(uri: Uri?)
    fun getNewPhotoFileName(): String
}