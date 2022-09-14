package com.topiichat.app.features.valid_phone_number.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.topiichat.app.AppActivity
import com.topiichat.app.core.extension.getPhoneNumber
import com.topiichat.app.core.extension.hideKeyboard
import com.topiichat.app.core.extension.showKeyboard
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentValidPhoneNumberBinding
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit
import javax.inject.Inject

@AndroidEntryPoint
class ValidPhoneNumberFragment : BaseFragment<FragmentValidPhoneNumberBinding>(),
    IValidPhoneNumberFragment {

    @Inject
    lateinit var factory: ValidPhoneNumberViewModel.AssistedFactory
    private val viewModel by viewModelCreator { factory.create() }
    private val phoneNumberKit by lazy {
        PhoneNumberKit.Builder(requireContext())
            .setIconEnabled(true)
            .build()
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentValidPhoneNumberBinding {
        return FragmentValidPhoneNumberBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ): Unit = with(binding) {
        setupPhoneNumberInputField(layoutEditText)
        initObservers()
        setupClickListener(nextAfterValidate, toolbar.btnBack, toolbar.btnClose)
    }

    override fun setupPhoneNumberInputField(inputLayout: TextInputLayout) {
        phoneNumberKit.attachToInput(binding.layoutEditText, viewModel.isoCode)
        phoneNumberKit.setupCountryPicker(
            activity = requireActivity() as AppActivity,
            searchEnabled = true
        )
    }

    override fun onResume() {
        super.onResume()
        with(binding.editText) {
            post { showKeyboard() }
        }
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
        when (v?.id) {
            binding.nextAfterValidate.id -> {
                viewModel.onVerifyPhoneNumberRequest(
                    binding.editText.getPhoneNumber(requireContext().applicationContext)
                )
            }
        }
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(visibilityTextError, ::onVisibilityTextError)
        observe(showMsgError, ::showErrorMessage)
        observe(hideKeyboard, ::onHideKeyboardEvent)
    }

    override fun onHideKeyboardEvent(ignore: Unit) {
        with(binding.editText) {
            post { hideKeyboard() }
        }
    }

    override fun onVisibilityTextError(isVisible: Boolean) = with(binding) {
        textViewError.isVisible = isVisible
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }
}