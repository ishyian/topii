package com.topiichat.app.features.remittance.presentation

import android.os.Parcelable
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class RemittanceParameters(
    val remittanceId: String,
    val toCountryName: String,
    val countryFlag: String,
    val remittance: RemittanceDomain? = null,
    val remittanceSendRequestTimeSeconds: Int = 15
) : Parcelable