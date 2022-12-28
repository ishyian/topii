package com.topiichat.app.features.kyc.address.domain

import com.topiichat.core.domain.Domain

data class AddressDomain(
    var address: String = "",
    var country: String = "",
    var region: String = "",
    var city: String = "",
    var postalCode: String = ""
) : Domain {
    fun isValid() = address.isNotEmpty() &&
        region.isNotEmpty() &&
        country.isNotEmpty() &&
        city.isNotEmpty() &&
        postalCode.isNotEmpty()
}