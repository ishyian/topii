package com.topiichat.app.core.extension

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.ContextWrapper
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.textfield.TextInputEditText
import com.redmadrobot.inputmask.MaskedTextChangedListener
import com.redmadrobot.inputmask.helper.AffinityCalculationStrategy
import com.topiichat.app.R
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

fun EditText.setupDateMask() {
    val dateFormat = "[00]{/}[00]{/}[0000]"
    val listener = MaskedTextChangedListener(dateFormat, this).apply {
        affineFormats = listOf(dateFormat)
        affinityCalculationStrategy = AffinityCalculationStrategy.PREFIX
        autocomplete = false
    }
    addTextChangedListener(listener)
}

fun Fragment.showSelectorDialog(
    title: String,
    items: List<String>,
    listener: (DialogInterface, Int) -> Unit
) {
    val dialog = AlertDialog.Builder(context!!)
    dialog.setTitle(title)
    dialog.setItems(items.toTypedArray(), listener)
    dialog.setNegativeButton(requireContext().getString(R.string.cancel)) { dialogInterface: DialogInterface, _: Int ->
        dialogInterface.dismiss()
    }
    dialog.show()
}

fun TextView.addStartDrawableFromUrl(url: String) {
    Glide.with(context)
        .load(url)
        .into(object : CustomTarget<Drawable?>(60, 60) {
            override fun onResourceReady(
                resource: Drawable,
                transition: com.bumptech.glide.request.transition.Transition<in Drawable?>?
            ) {
                drawableStart = resource
            }

            override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                drawableStart = placeholder
            }
        })
}

fun TextView.addStartCircleDrawableFromUrl(url: String) {
    Glide.with(context)
        .load(url)
        .circleCrop()
        .into(object : CustomTarget<Drawable?>(60, 60) {
            override fun onResourceReady(
                resource: Drawable,
                transition: com.bumptech.glide.request.transition.Transition<in Drawable?>?
            ) {
                drawableStart = resource
            }

            override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                drawableStart = placeholder
            }
        })
}

fun AutoCompleteTextView.showDropDownWhenClick() {
    setOnClickListener {
        showDropDown()
    }
}