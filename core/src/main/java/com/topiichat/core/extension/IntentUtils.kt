package com.topiichat.core.extension

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.annotation.RequiresPermission

private const val TELEPHONE_PREFIX = "tel:"

fun getCallPhoneIntent(number: String) = Intent(Intent.ACTION_CALL).apply {
    data = Uri.parse(TELEPHONE_PREFIX + number)
}

@RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
fun createGalleryIntent() = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

@RequiresPermission(Manifest.permission.CAMERA)
fun createCameraIntent() = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

@RequiresPermission(Manifest.permission.CAMERA)
fun getCameraIntent(uri: Uri) = createCameraIntent().apply {
    putExtra(MediaStore.EXTRA_OUTPUT, uri)
    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
}