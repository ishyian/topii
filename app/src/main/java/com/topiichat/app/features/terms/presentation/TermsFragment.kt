package com.topiichat.app.features.terms.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.topiichat.app.core.platform.BaseFragment
import com.topiichat.app.databinding.FragmentTermsBinding

class TermsFragment : BaseFragment<FragmentTermsBinding>(), ITermsFragment {

    private val viewModel: TermsViewModel by viewModels()

    override val tagId: Int get() = TODO("Not yet implemented")

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTermsBinding {
        return FragmentTermsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = with(binding) {
        initObservers()
        setupClickListener(termsUrl, nextScreen)
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(goActionView, ::onGoActionView)
        observe(showLoader, ::onVisibilityLoader)
        observe(navigate, ::onNavigate)
    }

    override fun onGoActionView(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onNavigate(actionId: Int) {
        currentActivity.navController.navigate(actionId)
    }
}