package com.topiichat.app.features.request_remittance.presentation

import android.os.Parcelable
import com.topiichat.app.features.home.domain.model.CountryDomain
import kotlinx.parcelize.Parcelize

@Parcelize
data class RequestRemittanceParameters(
    val country: CountryDomain
) : Parcelable