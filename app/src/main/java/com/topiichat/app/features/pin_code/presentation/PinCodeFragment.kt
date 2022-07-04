package com.topiichat.app.features.pin_code.presentation

import android.os.Bundle
import android.text.method.TransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentPinCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PinCodeFragment : BaseFragment<FragmentPinCodeBinding>(), IPinCodeFragment {

    @Inject
    lateinit var factory: PinCodeViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(argPhoneNumber, argPhoneCode, argAuthyId)
    }

    private val argPhoneNumber get() = arguments?.getString(ARG_PHONE_NUMBER) ?: ""
    private val argAuthyId get() = arguments?.getString(ARG_AUTHY_ID) ?: ""
    private val argPhoneCode get() = arguments?.getString(ARG_PHONE_CODE) ?: ""

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
        when (v?.id) {
            binding.nextAfterPinCode.id -> {
                viewModel.onCheckPinCode(binding.editTextPinCode.text.toString())
            }
        }
    }

    private fun initObservers() = with(viewModel) {
        observe(showPassTransformationMethod, ::onShowPassTransformationMethod)
        observe(showPassImage, ::onShowPassImage)
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
        observe(textPinCode, ::onTextPinCode)
        observe(visibilityTextContentTitle, ::onVisibilityTextContentTitle)
        observe(visibilityTextDescription, ::onVisibilityTextDescription)
        observe(visibilityTextError, ::onVisibilityTextError)
        observe(colorEditTextPinCode, ::onColorEditTextPinCode)
        observe(showMsgError, ::onShowMessageError)
    }

    override fun onShowPassTransformationMethod(
        transformationMethod: TransformationMethod
    ) = with(binding) {
        editTextPinCode.transformationMethod = transformationMethod
    }

    override fun onShowPassImage(imageId: Int) = with(binding) {
        btnShowPass.setImageResource(imageId)
    }

    override fun onTextPinCode(text: String) = with(binding) {
        editTextPinCode.setText(text)
    }

    override fun onVisibilityTextContentTitle(isVisible: Boolean) = with(binding) {
        textViewContentTitle.isVisible = isVisible
    }

    override fun onVisibilityTextDescription(isVisible: Boolean) = with(binding) {
        textViewDescription.isVisible = isVisible
    }

    override fun onVisibilityTextError(isVisible: Boolean) = with(binding) {
        textViewError.isVisible = isVisible
    }

    override fun onColorEditTextPinCode(colorId: Int) = with(binding) {
        editTextPinCode.setTextColor(ContextCompat.getColor(requireContext(), colorId))
    }

    override fun onShowMessageError(message: String) {
        showToast(message)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onNavigate(navigator: Navigator) {
        navigator.navigate(currentActivity.navController)
    }

    companion object {
        private const val ARG_PHONE_NUMBER = "phoneNumber"
        private const val ARG_AUTHY_ID = "authyId"
        private const val ARG_PHONE_CODE = "code"

        fun makeArgs(phoneNumber: String, authyId: String, code: String): Bundle {
            return Bundle(3).apply {
                putString(ARG_PHONE_NUMBER, phoneNumber)
                putString(ARG_AUTHY_ID, authyId)
                putString(ARG_PHONE_CODE, code)
            }
        }
    }
}