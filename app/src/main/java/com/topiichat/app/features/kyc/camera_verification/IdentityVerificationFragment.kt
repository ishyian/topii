package com.topiichat.app.features.kyc.camera_verification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.topiichat.app.core.contract.TakePhotoActivityContract
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentIdentityVerificationBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IdentityVerificationFragment : BaseFragment<FragmentIdentityVerificationBinding>(),
    IIdentityVerificationFragment {

    private val viewModel: IdentityVerificationViewModel by viewModels()
    private val takePhoto = registerForActivityResult(TakePhotoActivityContract()) { uri ->
        viewModel.onCameraImageSelected(uri)
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentIdentityVerificationBinding {
        return FragmentIdentityVerificationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        setupClickListener(btnTakePicture, toolbar.btnClose, toolbar.btnBack)
        initObservers()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    private fun initObservers() = with(viewModel) {
        observe(viewModel.takePhotoFromCamera, ::onTakePhotoFromCamera)
    }

    private fun onTakePhotoFromCamera(name: String) {
        takePhoto.launch(name)
    }
}