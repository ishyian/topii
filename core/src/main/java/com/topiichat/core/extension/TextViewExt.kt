package com.topiichat.core.extension

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.graphics.text.LineBreaker.JUSTIFICATION_MODE_INTER_WORD
import android.os.Build
import android.text.Html
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.text.parseAsHtml
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.material.animation.ArgbEvaluatorCompat

fun TextView.appendClickableSpan(clickableSpan: CharSequence) {
    setText(clickableSpan, TextView.BufferType.SPANNABLE)
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setHtmlText(string: String) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        this.setText(Html.fromHtml(string, Html.FROM_HTML_MODE_LEGACY), TextView.BufferType.SPANNABLE)
    } else {
        this.setText(string.parseAsHtml(), TextView.BufferType.SPANNABLE)
    }
}

fun TextView.setJustifyText() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        justificationMode = JUSTIFICATION_MODE_INTER_WORD
    }
}

var TextView.drawableStart: Drawable?
    get() = compoundDrawablesRelative[0]
    set(value) {
        val current = compoundDrawablesRelative
        setCompoundDrawablesRelativeWithIntrinsicBounds(value, current[1], current[2], current[3])
    }

var TextView.drawableEnd: Drawable?
    get() = compoundDrawablesRelative[2]
    set(value) {
        val current = compoundDrawablesRelative
        setCompoundDrawablesRelativeWithIntrinsicBounds(current[0], current[1], value, current[3])
    }

var TextView.textAndVisible: CharSequence?
    set(value) {
        text = value
        isGone = text.isNullOrBlank()
    }
    get() = text?.takeIf { isVisible }

fun TextView.animateTextColor(@ColorInt targetColor: Int, duration: Long = 150L): Animator? {
    val currentColor = currentTextColor
    return if (currentColor != targetColor) {
        val animator = ValueAnimator.ofObject(ArgbEvaluatorCompat.getInstance(), currentColor, targetColor)
        animator.duration = duration
        animator.addUpdateListener {
            setTextColor(it.animatedValue as Int)
        }
        animator.start()
        animator
    } else {
        null
    }
}

fun TextView.setTextOrHide(text: CharSequence?) {
    text?.let { this.text = text }
    isVisible = text != null
}

fun TextView.getLinkMovementMethod(action: (() -> Unit)?) = object : LinkMovementMethod() {
    override fun onTouchEvent(widget: TextView?, buffer: Spannable?, event: MotionEvent?): Boolean {
        if (event?.actionMasked == MotionEvent.ACTION_DOWN)
            action?.invoke()
        return super.onTouchEvent(widget, buffer, event)
    }
}

fun TextView.applyLineHeight(@DimenRes heightRes: Int) {
    val fontHeight = paint.getFontMetricsInt(null)
    setLineSpacing(context.resources.getDimension(heightRes) - fontHeight, 1F)
}

fun TextView.setTextSizeKtx(
    @DimenRes textSize: Int
) {
    setTextSize(
        TypedValue.COMPLEX_UNIT_PX,
        context.resources.getDimension(textSize)
    )
}

fun TextView.setTextColorKtx(@ColorRes colorRes: Int) {
    setTextColor(context.getColorKtx(colorRes))
}

fun TextView.setLinkTextColorKtx(@ColorRes colorRes: Int) {
    setLinkTextColor(context.getColorKtx(colorRes))
}