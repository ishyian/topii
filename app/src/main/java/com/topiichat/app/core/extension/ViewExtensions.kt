package com.topiichat.app.core.extension

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.ContextWrapper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.EditText
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.topiichat.app.features.valid_phone_number.presentation.model.PhoneNumber

fun TextInputEditText.getPhoneNumber(context: Context): PhoneNumber {
    val unformatted = text.toString().filter { !it.isWhitespace() && it != '-' }
    val phoneNumber = unformatted.parsePhoneNumber(context)
    val isoCode = phoneNumber.getRegionIsoCode(context)
    return PhoneNumber(
        number = phoneNumber?.nationalNumber?.toString(),
        code = "+${phoneNumber?.countryCode?.toString()}",
        isoCode = isoCode
    )
}

fun View.getActivity(): AppCompatActivity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is AppCompatActivity) {
            return context
        }
        context = context.baseContext
    }
    return null
}

fun EditText.showKeyboard() {
    if (requestFocus()) {
        (getActivity()?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
            .showSoftInput(this, SHOW_IMPLICIT)
        setSelection(text.length)
    }
}

fun EditText.hideKeyboard() {
    (getActivity()?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
        .hideSoftInputFromWindow(windowToken, 0)
}

fun ImageView.setTintColor(@ColorRes color: Int) {
    setColorFilter(
        ContextCompat.getColor(context, color),
        android.graphics.PorterDuff.Mode.SRC_IN
    )
}