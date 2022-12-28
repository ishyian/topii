package com.topiichat.app.features.home.data.model

import com.squareup.moshi.Json

data class RemittanceHistoryDto(
    @Json(name = "currency")
    val currency: String,
    @Json(name = "remittances")
    val remittances: List<Remittance>,
    @Json(name = "total")
    val total: Long,
    @Json(name = "totalReceived")
    val totalReceived: Long,
    @Json(name = "totalSent")
    val totalSent: Long
) {
    data class Remittance(
        @Json(name = "action")
        val action: String,
        @Json(name = "chapiiIdRecipient")
        val chapiiIdRecipient: String,
        @Json(name = "chapiiIdSender")
        val chapiiIdSender: String,
        @Json(name = "chapiiStatus")
        val chapiiStatus: List<Any>,
        @Json(name = "converting")
        val converting: Converting?,
        @Json(name = "createdAt")
        val createdAt: String,
        @Json(name = "description")
        val description: String,
        @Json(name = "exchangeRate")
        val exchangeRate: String,
        @Json(name = "fee")
        val fee: Fee?,
        @Json(name = "id")
        val id: String,
        @Json(name = "isNew")
        val isNew: Boolean,
        @Json(name = "purposedCode")
        val purposedCode: String?,
        @Json(name = "purposedCodeLabel")
        val purposedCodeLabel: String?,
        @Json(name = "receiving")
        val receiving: Receiving?,
        @Json(name = "recipient")
        val recipient: Recipient,
        @Json(name = "sender")
        val sender: Sender,
        @Json(name = "sending")
        val sending: Sending?,
        @Json(name = "status")
        val status: String,
        @Json(name = "transactionId")
        val transactionId: String?
    ) {
        data class Converting(
            @Json(name = "amount")
            val amount: Double,
            @Json(name = "currency")
            val currency: String
        )

        data class Fee(
            @Json(name = "amount")
            val amount: String,
            @Json(name = "currency")
            val currency: String
        )

        data class Receiving(
            @Json(name = "amount")
            val amount: Double,
            @Json(name = "currency")
            val currency: String
        )

        data class Recipient(
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
            val profile: Profile,
            @Json(name = "userIdAlice")
            val userIdAlice: String?
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
                val firstNameSecond: String,
                @Json(name = "lastName")
                val lastName: String,
                @Json(name = "lastNameSecond")
                val lastNameSecond: String,
                @Json(name = "province")
                val province: String
            )
        }

        data class Sender(
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
            val profile: Profile,
            @Json(name = "userIdAlice")
            val userIdAlice: String?
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
                val email: String,
                @Json(name = "firstName")
                val firstName: String,
                @Json(name = "firstNameSecond")
                val firstNameSecond: String,
                @Json(name = "lastName")
                val lastName: String,
                @Json(name = "lastNameSecond")
                val lastNameSecond: String,
                @Json(name = "province")
                val province: String
            )
        }

        data class Sending(
            @Json(name = "amount")
            val amount: String,
            @Json(name = "currency")
            val currency: String
        )
    }
}