package com.topiichat.app.features.otp.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.topiichat.app.R
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import com.topiichat.app.databinding.FragmentOtpBinding

class OtpFragment : BaseFragment<FragmentOtpBinding>(), IOtpFragment {

    private val viewModel: OtpViewModel by viewModels()

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
        setupClickListener(nextAfterOtp, toolbar.imageViewBack, toolbar.imageViewClose)
        firstPinView.requestFocus()
        toolbar.textViewTitle.text = getString(R.string.title_otp)
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onNavigate(navigator: Navigator) {
        navigator.navigate(currentActivity.navController)
    }

}