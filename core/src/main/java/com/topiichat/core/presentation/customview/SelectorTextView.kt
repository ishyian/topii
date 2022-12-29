package com.topiichat.core.presentation.customview

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.topiichat.core.R
import com.topiichat.core.databinding.ViewSelectorTextBinding

class SelectorTextView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: ViewSelectorTextBinding? = null
    private val binding get() = _binding ?: error("hint edit text binding is error")

    var text: String = ""
        get() = binding.etInput.text.toString()
        set(value) {
            field = value
            binding.etInput.text = value
        }
    var hint: String = ""
        get() = binding.textHint.text.toString()
        set(value) {
            field = value
            binding.textHint.text = value
        }

    val editText: TextView get() = binding.etInput

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : this(context, attrs, defStyleAttr, 0)

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : this(context, attrs, 0)

    constructor(
        context: Context
    ) : this(context, null)

    init {
        val inflater = LayoutInflater.from(context)
        inflater.inflate(R.layout.view_selector_text, this, true)
        _binding = ViewSelectorTextBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
        initUiParameters()
    }

    private fun initUiParameters() {
        background = ContextCompat.getDrawable(context, R.drawable.rounded_border_edittext)
        orientation = VERTICAL

        val paddingVertical = resources
            .getDimensionPixelSize(com.topiichat.core.R.dimen.hint_edit_text_padding_vertical)
        val paddingHorizontal = resources
            .getDimensionPixelSize(com.topiichat.core.R.dimen.hint_edit_text_padding_horizontal)
        setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical)

        setOnClickListener {
            editText.requestFocus()
        }
    }

    private fun initAttributes(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (attrs == null) return
        context.obtainStyledAttributes(
            attrs, R.styleable.SelectorTextView, defStyleAttr, defStyleRes
        ).apply {
            try {
                hint = getString(R.styleable.SelectorTextView_selector_hint) ?: ""
                text = getString(R.styleable.SelectorTextView_selector_text) ?: ""
            } finally {
                recycle()
            }
        }
    }
}