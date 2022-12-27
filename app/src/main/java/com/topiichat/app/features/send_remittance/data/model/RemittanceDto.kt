package com.topiichat.app.features.send_remittance.data.model

import com.squareup.moshi.Json
import com.topiichat.core.data.Dto

data class RemittanceDto(
    @Json(name = "action")
    val action: String,
    @Json(name = "chapiiIdRecipient")
    val chapiiIdRecipient: String,
    @Json(name = "chapiiIdSender")
    val chapiiIdSender: String,
    @Json(name = "converting")
    val converting: ConvertingDto,
    @Json(name = "createdAt")
    val createdAt: String,
    @Json(name = "description")
    val description: String,
    @Json(name = "exchangeRate")
    val exchangeRate: String,
    @Json(name = "fee")
    val fee: FeeDto,
    @Json(name = "id")
    val id: String,
    @Json(name = "isNew")
    val isNew: Boolean,
    @Json(name = "purposedCode")
    val purposedCode: String,
    @Json(name = "purposedCodeLabel")
    val purposedCodeLabel: String?,
    @Json(name = "receiving")
    val receiving: ReceivingDto,
    @Json(name = "recipient")
    val recipient: RecipientDto,
    @Json(name = "sender")
    val sender: SenderDto,
    @Json(name = "sending")
    val sending: SendingDto,
    @Json(name = "status")
    val status: String,
    @Json(name = "transactionId")
    val transactionId: String
)

data class ProfileDto(
    @Json(name = "avatar")
    val avatar: String,
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
    val province: String?
) : Dto

data class SendingDto(
    @Json(name = "amount")
    val amount: String,
    @Json(name = "currency")
    val currency: String
) : Dto

data class ConvertingDto(
    @Json(name = "amount")
    val amount: String,
    @Json(name = "currency")
    val currency: String
) : Dto

data class FeeDto(
    @Json(name = "amount")
    val amount: String,
    @Json(name = "currency")
    val currency: String
)

data class ReceivingDto(
    @Json(name = "amount")
    val amount: String,
    @Json(name = "currency")
    val currency: String
)

data class RecipientDto(
    @Json(name = "badgeCount")
    val badgeCount: Int,
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
    val profile: ProfileDto
)

data class SenderDto(
    @Json(name = "badgeCount")
    val badgeCount: Int,
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
    val profile: ProfileDto
)