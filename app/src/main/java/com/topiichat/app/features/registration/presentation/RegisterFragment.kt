package com.topiichat.app.features.registration.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentRegisterBinding
import com.topiichat.app.features.registration.presentation.model.BtnRegisterEnablingUi
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(), IRegisterFragment,
    CompoundButton.OnCheckedChangeListener {

    @Inject
    lateinit var factory: RegisterViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(argPhoneNumber, argPhoneCode, argAuthyId, argPinCode)
    }

    private val argPhoneNumber get() = arguments?.getString(ARG_PHONE_NUMBER) ?: ""
    private val argAuthyId get() = arguments?.getString(ARG_AUTHY_ID) ?: ""
    private val argPhoneCode get() = arguments?.getString(ARG_PHONE_CODE) ?: ""
    private val argPinCode get() = arguments?.getString(ARG_PIN_CODE) ?: ""

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentRegisterBinding {
        return FragmentRegisterBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ): Unit = with(binding) {
        initObservers()
        setupClickListener(btnRegister, toolbar.btnBack, toolbar.btnClose)
        switch1.setOnCheckedChangeListener(this@RegisterFragment)
        switch2.setOnCheckedChangeListener(this@RegisterFragment)
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onCheckedChanged(view: CompoundButton?, isChecked: Boolean) {
        viewModel.onCheckedChanged(view?.id, isChecked)
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
        observe(btnRegisterEnabling, ::onEnablingBtnRegister)
        observe(showMsgError, ::onShowMessageError)
    }

    override fun onEnablingBtnRegister(
        btnRegisterEnabling: BtnRegisterEnablingUi
    ) = with(binding.btnRegister) {
        isEnabled = btnRegisterEnabling.isEnabled
        setBackgroundResource(btnRegisterEnabling.backgroundId)
    }

    override fun onShowMessageError(message: String) {
        showToast(message)
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
        private const val ARG_AUTHY_ID = "authyId"
        private const val ARG_PHONE_CODE = "code"
        private const val ARG_PIN_CODE = "pinCode"

        fun makeArgs(
            phoneNumber: String,
            authyId: String,
            code: String,
            pinCode: String
        ): Bundle {
            return Bundle(4).apply {
                putString(ARG_PHONE_NUMBER, phoneNumber)
                putString(ARG_AUTHY_ID, authyId)
                putString(ARG_PHONE_CODE, code)
                putString(ARG_PIN_CODE, pinCode)
            }
        }
    }
}