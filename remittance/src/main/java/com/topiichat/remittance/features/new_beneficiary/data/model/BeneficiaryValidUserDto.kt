package com.topiichat.remittance.features.new_beneficiary.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class BeneficiaryValidUserDto(
    @Json(name = "exist")
    val exist: Boolean,
    @Json(name = "message")
    val message: String,
    @Json(name = "user")
    val user: User?
) : Dto {
    data class User(
        @Json(name = "completePhoneNumber")
        val completePhoneNumber: String,
        @Json(name = "createdAt")
        val createdAt: String,
        @Json(name = "dialCountryCode")
        val dialCountryCode: String,
        @Json(name = "id")
        val id: String,
        @Json(name = "isActive")
        val isActive: Boolean,
        @Json(name = "nickName")
        val nickName: String,
        @Json(name = "phoneNumber")
        val phoneNumber: String,
        @Json(name = "profile")
        val profile: Profile
    ) : Dto {
        data class Profile(
            @Json(name = "avatar")
            val avatar: String?,
            @Json(name = "birthDate")
            val birthDate: String,
            @Json(name = "buckzyPartyId")
            val buckzyPartyId: String,
            @Json(name = "buckzyPartyStatus")
            val buckzyPartyStatus: String,
            @Json(name = "city")
            val city: String,
            @Json(name = "email")
            val email: String,
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
}