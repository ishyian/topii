package com.topiichat.app.features.pin_code.presentation

import android.os.Bundle
import android.text.method.TransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentPinCodeBinding

class PinCodeFragment : BaseFragment<FragmentPinCodeBinding>(), IPinCodeFragment {

    private val viewModel: PinCodeViewModel by viewModels()

    override val tagId: Int get() = TODO("Not yet implemented")

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPinCodeBinding {
        return FragmentPinCodeBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ): Unit = with(binding) {
        initObservers()
        setupClickListener(
            btnShowPass, nextAfterPinCode, toolbar.btnBack, toolbar.btnClose
        )
        editTextPinCode.requestFocus()

    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(showPassTransformationMethod, ::onShowPassTransformationMethod)
        observe(showPassImage, ::onShowPassImage)
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
    }

    override fun onShowPassTransformationMethod(
        transformationMethod: TransformationMethod
    ) = with(binding) {
        editTextPinCode.transformationMethod = transformationMethod
    }

    override fun onShowPassImage(imageId: Int) = with(binding) {
        btnShowPass.setImageResource(imageId)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onNavigate(navigator: Navigator) {
        navigator.navigate(currentActivity.navController)
    }
}