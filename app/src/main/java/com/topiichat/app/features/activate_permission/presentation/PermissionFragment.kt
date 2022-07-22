package com.topiichat.app.features.activate_permission.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentPermissionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PermissionFragment : BaseFragment<FragmentPermissionBinding>(), IPermissionFragment {

    private val viewModel: PermissionViewModel by viewModels()

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentPermissionBinding {
        return FragmentPermissionBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = with(binding) {
        initObservers()
        setupPermissions()
        setupClickListener(nextAfterPermission, toolbar.btnBack, toolbar.btnClose)
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(showLoader, ::onVisibilityLoader)
    }

    private fun setupPermissions() {
        val permissionAudio = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        )
        val permissionCamera = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        )

        val permissionStorage = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        if (permissionAudio != PackageManager.PERMISSION_GRANTED ||
            permissionCamera != PackageManager.PERMISSION_GRANTED ||
            permissionStorage != PackageManager.PERMISSION_GRANTED   ) {
            Log.i("TAG", "Permission to record denied")
            makePermissions()
        }
    }

    private fun makePermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ),
            RECORD_REQUEST_CODE
        )
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    companion object {
        private const val RECORD_REQUEST_CODE = 1
    }
}