package com.topiichat.core.contract

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import com.topiichat.core.extension.createCameraIntent
import com.topiichat.core.extension.getCameraIntent
import com.topiichat.core.extension.toPublicUri
import java.io.File

class TakePhotoActivityContract : ActivityResultContract<String, Uri?>() {

    private var cameraOutputFile: File? = null

    @RequiresPermission(Manifest.permission.CAMERA)
    override fun createIntent(context: Context, input: String): Intent {
        cameraOutputFile = File(context.cacheDir, input)
        val fileUri = checkNotNull(cameraOutputFile).toPublicUri(context)

        context.packageManager
            .queryIntentActivities(createCameraIntent(), PackageManager.MATCH_DEFAULT_ONLY)
            .forEach { info ->
                val packageName = info.activityInfo.packageName
                context.grantUriPermission(packageName, fileUri, MODE_FLAGS)
            }

        return getCameraIntent(fileUri)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return cameraOutputFile?.toUri()
    }

    private companion object {
        private const val MODE_FLAGS = Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
            Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
}