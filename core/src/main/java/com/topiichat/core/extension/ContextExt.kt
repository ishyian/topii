package com.topiichat.core.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.PluralsRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat

fun Context.getColorKtx(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun Context.getDrawableKtx(@DrawableRes id: Int): Drawable? {
    return AppCompatResources.getDrawable(this, id)
}

fun Context.getColorStateListKtx(@ColorRes id: Int): ColorStateList {
    return AppCompatResources.getColorStateList(this, id)
}

fun Context.getQuantityStringKtx(@PluralsRes id: Int, quantity: Int, vararg values: Any?): String {
    return if (values.isNotEmpty()) {
        resources.getQuantityString(id, quantity, *values)
    } else {
        resources.getQuantityString(id, quantity, quantity)
    }
}

fun Context.getDimensionKtx(@DimenRes id: Int): Float {
    return resources.getDimension(id)
}

fun Context.getDimensionPixelSizeKtx(@DimenRes id: Int): Int {
    return resources.getDimensionPixelSize(id)
}

fun Context.isColor(@ColorRes colorResId: Int): Boolean {
    val value = TypedValue()
    resources.getValue(colorResId, value, true)

    return (value.type >= TypedValue.TYPE_FIRST_COLOR_INT &&
        value.type <= TypedValue.TYPE_LAST_COLOR_INT)
}

fun Context.copyToClipboard(text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label", text)
    clipboard.setPrimaryClip(clip)
}

fun Context.hasPermissions(vararg permissions: String) = permissions.all { isPermissionGranted(it) }

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.dpToPx(dp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        resources.displayMetrics
    )
}

fun Context.dpToPx(dp: Int): Float {
    return dpToPx(dp.toFloat())
}

private fun openInExternalBrowser(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    if (intent.resolveActivity(context.packageManager) != null) {
        context.startActivity(intent)
    }
}