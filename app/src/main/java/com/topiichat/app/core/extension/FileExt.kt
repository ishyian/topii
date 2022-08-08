package com.topiichat.app.core.extension

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.topiichat.app.BuildConfig
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.util.Locale

fun File.toPublicUri(context: Context): Uri {
    return FileProvider.getUriForFile(context, "${BuildConfig.APPLICATION_ID}.provider", this)
}

fun File.toBase64(): String = Base64.encodeToString(readBytes(), Base64.NO_WRAP)

fun File.getMimeType(fallback: String = "image/*"): String {
    return MimeTypeMap.getFileExtensionFromUrl(path)?.let {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(it.lowercase(Locale.ENGLISH))
    } ?: fallback
}

fun Closeable?.closeQuietly() {
    try {
        this?.close()
    } catch (ignored: IOException) {
    }
}