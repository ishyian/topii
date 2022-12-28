package com.topiichat.app.features.registration.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.core.view.isVisible
import com.topiichat.app.databinding.FragmentRegisterBinding
import com.topiichat.app.features.registration.presentation.model.BtnRegisterEnablingUi
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(), IRegisterFragment,
    CompoundButton.OnCheckedChangeListener {

    @Inject
    lateinit var factory: RegisterViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: RegisterParameters by parcelableParameters()

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
        observe(btnRegisterEnabling, ::onEnablingBtnRegister)
        observe(showMsgError, ::showErrorMessage)
    }

    override fun onEnablingBtnRegister(
        btnRegisterEnabling: BtnRegisterEnablingUi
    ) = with(binding.btnRegister) {
        isEnabled = btnRegisterEnabling.isEnabled
        setBackgroundResource(btnRegisterEnabling.backgroundId)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }
}