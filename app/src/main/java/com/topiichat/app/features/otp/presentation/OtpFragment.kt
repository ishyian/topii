package com.topiichat.app.features.otp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.topiichat.app.R
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentOtpBinding
import com.topiichat.app.features.otp.presentation.model.BtnSendSmsEnablingUi
import com.topiichat.app.features.otp.presentation.model.TextSendSmsTimerUi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtpFragment : BaseFragment<FragmentOtpBinding>(), IOtpFragment {

    @Inject
    internal lateinit var factory: OtpViewModelFactory
    private val viewModel: OtpViewModel by viewModels { factory }
    private val argPhoneNumber get() = arguments?.getString(ARG_PHONE_NUMBER) ?: ""

    override val tagId: Int get() = TODO("Not yet implemented")

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentOtpBinding {
        return FragmentOtpBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ): Unit = with(binding) {
        initObservers()
        setupClickListener(btnNextAfterOtp, btnSendSms, toolbar.btnBack, toolbar.btnClose)
        pinView.requestFocus()
        textViewTitleWithPhoneNumber.text = getString(R.string.subtitle_otp, argPhoneNumber)
    }

    override fun onClick(v: View?) = with(binding) {
        viewModel.onClick(v)
        when (v?.id) {
            btnNextAfterOtp.id -> {
                onValidPinCodeRequest()
            }
            btnSendSms.id -> {
                onSendSms()
            }
        }
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
        observe(colorPinView, ::onColorPinView)
        observe(visibilityTextError, ::onVisibilityTextError)
        observe(showMsgError, ::onShowMessageError)
        observe(btnSendSmsEnabling, ::onEnablingBtnSendSms)
        observe(textSendSmsTimer, ::onTimerTextSendSms)
    }

    override fun onValidPinCodeRequest() {
        viewModel.onValidPinCodeRequest(binding.pinView.text.toString())
    }

    override fun onSendSms() {
        viewModel.onSendSms(argPhoneNumber)
    }

    override fun onShowMessageError(message: String) {
        showToast(message)
    }

    override fun onColorPinView(colorId: Int) = with(binding) {
        pinView.setTextColor(ContextCompat.getColor(requireContext(), colorId))
    }

    override fun onVisibilityTextError(isVisible: Boolean) = with(binding) {
        textViewError.isVisible = isVisible
    }

    override fun onEnablingBtnSendSms(
        btnSendSmsEnabling: BtnSendSmsEnablingUi
    ): Unit = with(binding) {
        btnSendSmsEnabling.apply {
            btnSendSms.isEnabled = isEnabled
            btnSendSms.setTextColor(ContextCompat.getColor(requireContext(), colorId))
        }
    }

    override fun onTimerTextSendSms(
        textSendSmsTimer: TextSendSmsTimerUi
    ): Unit = with(binding) {
        textSendSmsTimer.apply {
            textViewTimer.isVisible = isVisible
            textViewTimer.setTextColor(ContextCompat.getColor(requireContext(), colorId))
            textViewTimer.text = getString(R.string.timer_otp, time)
        }
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    override fun onNavigate(navigator: Navigator) {
        navigator.navigate(currentActivity.navController)
    }

    companion object {
        private const val ARG_PHONE_NUMBER = "phoneNumber"

        fun makeArgs(phoneNumber: String): Bundle {
            return Bundle(1).apply {
                putString(ARG_PHONE_NUMBER, phoneNumber)
            }
        }
    }
}