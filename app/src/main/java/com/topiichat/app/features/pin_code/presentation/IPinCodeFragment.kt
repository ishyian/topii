package com.topiichat.app.features.pin_code.presentation

import android.text.method.TransformationMethod
import com.topiichat.app.core.presentation.platform.IBaseFragment

interface IPinCodeFragment : IBaseFragment {
    fun onShowPassTransformationMethod(transformationMethod: TransformationMethod)
    fun onShowPassImage(imageId: Int)
}