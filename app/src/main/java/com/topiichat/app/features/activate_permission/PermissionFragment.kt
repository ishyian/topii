package com.topiichat.app.features.activate_permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.topiichat.app.R
import com.topiichat.app.core.extension.Constants
import com.topiichat.app.core.navigation.Navigator
import com.topiichat.app.core.platform.BaseFragment
import javax.inject.Inject


class PermissionFragment : BaseFragment() {

    @Inject
    lateinit var navigator: Navigator

    override fun layoutId() = R.layout.fragment_permission

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPermissions()

        val nextScreen : Button = view.findViewById(R.id.next_after_permission)
        nextScreen.setOnClickListener {
            navigator = Navigator()
            navigator.showValidatePhone(view.context)
        }
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
            makeRequestAudio()
            makeRequestCamera()
            makeRequestStorage()
        }
    }

    private fun makeRequestStorage() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            Constants.RECORD_REQUEST_CODE
        )
    }
    private fun makeRequestCamera() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            Constants.RECORD_REQUEST_CODE
        )
    }
    private fun makeRequestAudio() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            Constants.RECORD_REQUEST_CODE
        )

    }
}