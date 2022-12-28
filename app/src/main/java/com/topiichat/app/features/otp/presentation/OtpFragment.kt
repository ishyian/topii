package com.topiichat.app.features.otp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.topiichat.app.R
import com.topiichat.app.databinding.FragmentOtpBinding
import com.topiichat.app.features.otp.presentation.model.BtnSendSmsEnablingUi
import com.topiichat.app.features.otp.presentation.model.TextSendSmsTimerUi
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.hideKeyboard
import com.topiichat.core.extension.showKeyboard
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OtpFragment : BaseFragment<FragmentOtpBinding>(), IOtpFragment {

    @Inject
    lateinit var factory: OtpViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: OtpParameters by parcelableParameters()

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
        textViewTitleWithPhoneNumber.text =
            getString(R.string.subtitle_otp, parameters.code.plus(parameters.phoneNumber))
    }

    override fun onResume() {
        super.onResume()
        with(binding.pinView) {
            post { showKeyboard() }
        }
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
        observe(colorPinView, ::onColorPinView)
        observe(visibilityTextError, ::onVisibilityTextError)
        observe(showMsgError, ::showErrorMessage)
        observe(btnSendSmsEnabling, ::onEnablingBtnSendSms)
        observe(textSendSmsTimer, ::onTimerTextSendSms)
        observe(hideKeyboard, ::onHideKeyboardEvent)
    }

    override fun onHideKeyboardEvent(nothing: Unit) {
        with(binding.pinView) {
            post { hideKeyboard() }
        }
    }

    override fun onValidPinCodeRequest() {
        viewModel.onValidOtpCodeRequest(binding.pinView.text.toString())
    }

    override fun onSendSms() {
        viewModel.onSendSms()
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
}