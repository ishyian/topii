package com.topiichat.app.features.send_remittance.presentation

import android.os.Parcelable
import com.topiichat.app.features.home.domain.model.CountryDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class SendRemittanceParameters(
    val country: CountryDomain
) : Parcelable