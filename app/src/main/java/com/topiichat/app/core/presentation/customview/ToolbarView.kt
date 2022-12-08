package com.topiichat.app.core.presentation.customview

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue.COMPLEX_UNIT_PX
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isInvisible
import com.topiichat.app.databinding.PartToolbarBinding
import com.topiichat.core.R

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
        inflater.inflate(com.topiichat.app.R.layout.part_toolbar, this, true)
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
            attrs, com.topiichat.app.R.styleable.ToolbarView, defStyleAttr, defStyleRes
        ).apply {
            try {
                binding.textViewTitle.text = getString(com.topiichat.app.R.styleable.ToolbarView_toolbarTitle)
                binding.imageViewBack.isInvisible =
                    !getBoolean(com.topiichat.app.R.styleable.ToolbarView_isBackButtonVisible, true)
                binding.imageViewClose.isInvisible =
                    !getBoolean(com.topiichat.app.R.styleable.ToolbarView_isCloseButtonVisible, true)
                binding.textViewTitle.setTextSize(
                    COMPLEX_UNIT_PX,
                    getDimensionPixelSize(
                        com.topiichat.app.R.styleable.ToolbarView_textSize,
                        resources.getDimensionPixelSize(R.dimen.toolbar_view_default_text_size)
                    ).toFloat()
                )
            } finally {
                recycle()
            }
        }
    }
}