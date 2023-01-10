package com.topiichat.app.features.terms.presentation

import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.topiichat.app.databinding.FragmentTermsBinding
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TermsFragment : BaseFragment<FragmentTermsBinding>(), ITermsFragment {

    private val viewModel: TermsViewModel by viewModels()

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PERMISSION_GRANTED
            ) {

                requireActivity().requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 12345)
            }
        }
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(goActionView, ::onGoActionView)
        observe(showLoader, ::onVisibilityLoader)
    }

    override fun onGoActionView(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit
}