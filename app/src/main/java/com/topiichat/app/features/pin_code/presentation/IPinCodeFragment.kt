package com.topiichat.app.features.pin_code.presentation

import android.text.method.TransformationMethod
import com.topiichat.core.presentation.platform.IBaseFragment

interface IPinCodeFragment : IBaseFragment {
    fun onShowPassTransformationMethod(transformationMethod: TransformationMethod)
    fun onShowPassImage(imageId: Int)
    fun onTextPinCode(text: String)
    fun onVisibilityTextContentTitle(isVisible: Boolean)
    fun onVisibilityTextDescription(isVisible: Boolean)
    fun onVisibilityTextError(isVisible: Boolean)
    fun onColorEditTextPinCode(colorId: Int)
}