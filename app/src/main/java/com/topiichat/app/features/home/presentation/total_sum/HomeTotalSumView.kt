package com.topiichat.app.features.home.presentation.total_sum

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.topiichat.app.R
import com.topiichat.app.databinding.ViewHomeTotalSumBinding

class HomeTotalSumView(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int,
    defStyleRes: Int
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var _binding: ViewHomeTotalSumBinding? = null
    private val binding get() = _binding ?: error("home total sum binding is error")

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
        inflater.inflate(R.layout.view_home_total_sum, this, true)
        _binding = ViewHomeTotalSumBinding.bind(this)
        binding.root.background = ContextCompat.getDrawable(context, R.drawable.bg_home_total_sum)
    }

    @SuppressLint("SetTextI18n")
    fun renderWith(totalSentSum: Long, totalRequestedSum: Long, currency: String) {
        binding.textTotalSentSum.text = "$currency $totalSentSum"
        binding.textTotalRequestedSum.text = "$currency $totalRequestedSum"
    }
}