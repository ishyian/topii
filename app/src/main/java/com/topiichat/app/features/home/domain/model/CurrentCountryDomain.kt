package com.topiichat.app.features.home.domain.model

import android.os.Parcelable
import com.topiichat.core.domain.Domain
import kotlinx.parcelize.Parcelize

@Parcelize
data class CurrentCountryDomain(
    val isAvailable: Boolean,
    val countryInfo: CountryDomain? = null
) : Domain, Parcelable