package com.topiichat.app.features.home.data.model

import com.squareup.moshi.Json

data class RecentUsersDto(
    @Json(name = "recentUsers")
    val recentUsers: List<User>,
    @Json(name = "total")
    val total: Int
) {
    data class User(
        @Json(name = "dialCountryCode")
        val dialCountryCode: String,
        @Json(name = "id")
        val id: String,
        @Json(name = "nickName")
        val nickName: String?,
        @Json(name = "phoneNumber")
        val phoneNumber: String,
        @Json(name = "profile")
        val profile: Profile
    ) {
        data class Profile(
            @Json(name = "avatar")
            val avatar: String,
            @Json(name = "birthDate")
            val birthDate: String?,
            @Json(name = "buckzyPartyId")
            val buckzyPartyId: String?,
            @Json(name = "buckzyPartyStatus")
            val buckzyPartyStatus: String?,
            @Json(name = "city")
            val city: String,
            @Json(name = "email")
            val email: String?,
            @Json(name = "firstName")
            val firstName: String,
            @Json(name = "firstNameSecond")
            val firstNameSecond: String?,
            @Json(name = "lastName")
            val lastName: String,
            @Json(name = "lastNameSecond")
            val lastNameSecond: String?,
            @Json(name = "province")
            val province: String
        )
    }
}