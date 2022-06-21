package com.topiichat.app.features.valid_phone_number.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.topiichat.app.core.presentation.navigation.Navigator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentValidPhoneNumberBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ValidPhoneNumberFragment : BaseFragment<FragmentValidPhoneNumberBinding>(),
    IValidPhoneNumberFragment {

    @Inject
    internal lateinit var factory: ValidPhoneNumberViewModelFactory
    private val viewModel: ValidPhoneNumberViewModel by viewModels { factory }

    override val tagId: Int get() = TODO("Not yet implemented")

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
        initObservers()
        setupClickListener(nextAfterValidate, toolbar.btnBack, toolbar.btnClose)
        editText.requestFocus()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
        when (v?.id) {
            binding.nextAfterValidate.id -> {
                viewModel.onValidPhoneNumberRequest(binding.editText.text.toString())
            }
        }
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
        observe(visibilityTextError, ::onVisibilityTextError)
        observe(showMsgError, ::onShowMessageError)
    }

    override fun onShowMessageError(message: String) {
        showToast(message)
    }

    override fun onVisibilityTextError(isVisible: Boolean) = with(binding) {
        textViewError.isVisible = isVisible
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    override fun onNavigate(navigator: Navigator) {
        navigator.navigate(currentActivity.navController)
    }

}