package com.topiichat.app.core.presentation.customview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import com.topiichat.app.R
import com.topiichat.app.databinding.PartToolbarBinding

class ToolbarView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: PartToolbarBinding? = null
    private val binding get() = _binding ?: error("toolbar binding is error")

    val btnBack get() = binding.imageViewBack
    val btnClose get() = binding.imageViewClose

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
        inflater.inflate(R.layout.part_toolbar, this, true)
        _binding = PartToolbarBinding.bind(this)
        initAttributes(attrs, defStyleAttr, defStyleRes)
    }

    private fun initAttributes(
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) {
        if (attrs == null) return
        context.obtainStyledAttributes(
            attrs, R.styleable.ToolbarView, defStyleAttr, defStyleRes
        ).apply {
            try {
                binding.textViewTitle.text = getString(R.styleable.ToolbarView_text)
                binding.imageViewBack.isInvisible = !getBoolean(R.styleable.ToolbarView_isBackButtonVisible, true)
                binding.imageViewClose.isInvisible = !getBoolean(R.styleable.ToolbarView_isCloseButtonVisible, true)
                binding.textViewTitle.setTextSize(
                    COMPLEX_UNIT_PX,
                    getDimensionPixelSize(
                        R.styleable.ToolbarView_textSize,
                        resources.getDimensionPixelSize(R.dimen.toolbar_view_default_text_size)
                    ).toFloat()
                )
            } finally {
                recycle()
            }
        }
    }
}