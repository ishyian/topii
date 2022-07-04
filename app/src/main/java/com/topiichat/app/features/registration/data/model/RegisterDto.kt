package com.topiichat.app.features.registration.data.model

import com.squareup.moshi.Json
import com.topiichat.app.core.data.Dto

data class RegisterDto(
    @Json(name = "accessToken")
    val accessToken: String?,
    @Json(name = "badgeCount")
    val badgeCount: String?,
    @Json(name = "dialCountryCode")
    val dialCountryCode: String?,
    @Json(name = "firebaseUID")
    val firebaseUID: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "isActive")
    val isActive: Boolean,
    @Json(name = "nickName")
    val nickName: String?,
    @Json(name = "phoneNumber")
    val phoneNumber: String?,
    @Json(name = "profile")
    val profile: Profile?,
    @Json(name = "pushKitToken")
    val pushKitToken: String?,
    @Json(name = "pushToken")
    val pushToken: String?,
    @Json(name = "streamToken")
    val streamToken: String?
) : Dto {
    data class Profile(
        @Json(name = "avatar")
        val avatar: String?,
        @Json(name = "birthDate")
        val birthDate: String?,
        @Json(name = "buckzyPartyId")
        val buckzyPartyId: String?,
        @Json(name = "buckzyPartyStatus")
        val buckzyPartyStatus: String?,
        @Json(name = "city")
        val city: String?,
        @Json(name = "email")
        val email: String?,
        @Json(name = "firstName")
        val firstName: String?,
        @Json(name = "firstNameSecond")
        val firstNameSecond: String?,
        @Json(name = "lastName")
        val lastName: String?,
        @Json(name = "lastNameSecond")
        val lastNameSecond: String?,
        @Json(name = "province")
        val province: String?
    ) : Dto
}