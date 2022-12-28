package com.topiichat.app.features.home.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.topiichat.core.data.Dto

@JsonClass(generateAdapter = true)
data class AvailableCountryDto(
    @Json(name = "allowedFrom")
    val allowedFrom: Boolean,
    @Json(name = "allowedTo")
    val allowedTo: Boolean,
    @Json(name = "code")
    val code: String,
    @Json(name = "currencyCode")
    val currencyCode: String,
    @Json(name = "dialCodeNumber")
    val dialCodeNumber: String,
    @Json(name = "flag")
    val flag: String,
    @Json(name = "id")
    val id: Int,
    @Json(name = "kycFields")
    val kycFields: List<Any>,
    @Json(name = "limitMax")
    val limitMax: String,
    @Json(name = "limitMin")
    val limitMin: String,
    @Json(name = "name")
    val name: String,
    @Json(name = "preferred")
    val preferred: Boolean
) : Dto