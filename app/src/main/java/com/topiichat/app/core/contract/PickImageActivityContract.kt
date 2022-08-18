package com.topiichat.app.core.contract

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.RequiresPermission
import com.topiichat.app.core.utils.createGalleryIntent

class PickImageActivityContract : ActivityResultContract<Unit, Uri?>() {

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    override fun createIntent(context: Context, input: Unit?): Intent {
        return createGalleryIntent()
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (intent == null || resultCode != Activity.RESULT_OK) {
            return null
        }

        return intent.data
    }
}