package com.topiichat.app.features.registration.data.mapper

import com.topiichat.app.features.registration.data.model.RegisterDto
import com.topiichat.app.features.registration.domain.model.ProfileDomain
import com.topiichat.app.features.registration.domain.model.RegisterDomain
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class RegisterRemoteMapper @Inject constructor() : Mapper<RegisterDto, RegisterDomain> {
    override fun map(input: RegisterDto?): RegisterDomain {
        return RegisterDomain(
            accessToken = input?.accessToken ?: "",
            senderId = input?.id ?: "",
            profile = ProfileDomain(
                avatar = input?.profile?.avatar ?: "",
                lastName = input?.profile?.lastName ?: "",
                firstName = input?.profile?.firstName ?: "",
                firstNameSecond = input?.profile?.firstNameSecond ?: "",
                lastNameSecond = input?.profile?.lastNameSecond ?: ""
            )
        )
    }
}