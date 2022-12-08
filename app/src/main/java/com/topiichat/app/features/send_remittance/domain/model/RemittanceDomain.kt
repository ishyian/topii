package com.topiichat.app.features.send_remittance.domain.model

import android.os.Parcelable
import com.topiichat.app.features.send_remittance.data.model.ProfileDto
import com.topiichat.core.domain.Domain
import kotlinx.parcelize.Parcelize
import org.threeten.bp.LocalDateTime

@Parcelize
data class RemittanceDomain(
    val action: String,
    val recipientId: String,
    val senderId: String,
    val createdAt: LocalDateTime,
    val description: String,
    val id: String,
    val exchangeRate: Double,
    val transactionId: String,
    val converting: ConvertingDomain,
    val fee: FeeDomain,
    val receiving: ReceivingDomain,
    val sending: SendingDomain,
    val recipient: RecipientDomain,
    val sender: SenderDomain,
    val status: String
) : Domain, Parcelable {
    fun exchangeRateText(): String {
        val exchangeRecipientCurrencyRate = 1 * exchangeRate
        return "1${sending.currency}=$exchangeRecipientCurrencyRate${receiving.currency}"
    }

    fun maskedIdText(): String {
        return "ID: ***${id.takeLast(6)}"
    }
}

@Parcelize
data class ConvertingDomain(
    val amount: Double,
    val currency: String
) : Domain, Parcelable

@Parcelize
data class FeeDomain(
    val amount: Double,
    val currency: String
) : Domain, Parcelable {
    fun feeText() = "$amount$currency"
}

@Parcelize
data class ReceivingDomain(
    val amount: Double,
    val currency: String
) : Domain, Parcelable {
    fun receivingText() = "$amount$currency"
}

@Parcelize
data class RecipientDomain(
    val id: String,
    val isActive: Boolean,
    val profile: ProfileDomain
) : Domain, Parcelable

@Parcelize
data class SenderDomain(
    val id: String,
    val isActive: Boolean,
    val profile: ProfileDomain
) : Domain, Parcelable

@Parcelize
data class ProfileDomain(
    val avatar: String,
    val city: String,
    val email: String,
    val firstName: String,
    val firstNameSecond: String?,
    val lastName: String,
    val lastNameSecond: String?,
    val province: String
) : Domain, Parcelable {
    fun fullName() = "$firstName $lastName"
}

@Parcelize
data class SendingDomain(
    val amount: Double,
    val currency: String
) : Domain, Parcelable {
    fun sendingText() = "$amount$currency"
}

fun ProfileDto.toDomain(): ProfileDomain {
    return ProfileDomain(
        avatar = avatar,
        city = city,
        email = email ?: "",
        firstName = firstName,
        firstNameSecond = firstNameSecond,
        lastName = lastName,
        lastNameSecond = lastNameSecond,
        province = province ?: ""
    )
}