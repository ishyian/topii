package com.topiichat.app.features.home.domain.model

import android.os.Parcelable
import com.topiichat.app.core.domain.Domain
import kotlinx.parcelize.Parcelize

@Parcelize
data class CountryDomain(
    val allowedFrom: Boolean,
    val allowedTo: Boolean,
    val code: String,
    val currencyCode: String,
    val flagImageUrl: String,
    val kycFields: List<String>,
    val limitMax: Double,
    val limitMin: Double,
    val name: String,
    val preferred: Boolean
) : Domain, Parcelable {
    val countryCode: CountryCode
        get() = CountryCode.values().firstOrNull { it.name == code } ?: CountryCode.UNAVAILABLE
}

enum class CountryCode {
    US, GT, UNAVAILABLE
}
