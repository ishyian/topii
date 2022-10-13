package com.topiichat.app.features.wallet.wallet_balance.marker

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.topiichat.app.R

@SuppressLint("ViewConstructor")
class WalletMarkerView(context: Context) :
    MarkerView(context, R.layout.view_wallet_marker) {

    private val tvContent: TextView by lazy {
        findViewById(R.id.text_amount)
    }

    override fun refreshContent(e: Entry, highlight: Highlight) {
        tvContent.text =
            String.format(
                "%s $",
                e.y
            )
        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }

}
