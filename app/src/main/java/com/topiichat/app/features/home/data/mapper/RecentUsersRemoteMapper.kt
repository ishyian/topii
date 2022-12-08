package com.topiichat.app.features.home.data.mapper

import com.topiichat.app.features.home.data.model.RecentUsersDto
import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class RecentUsersRemoteMapper @Inject constructor() :
    Mapper<RecentUsersDto, List<RecentUserDomain>> {
    override fun map(input: RecentUsersDto?): List<RecentUserDomain> {
        return input?.recentUsers?.map { user ->
            RecentUserDomain(
                avatar = user.profile.avatar,
                recipientId = user.id,
                dialCode = user.dialCountryCode,
                fullName = "${user.profile.firstName} ${user.profile.lastName}"
            )
        } ?: emptyList()
    }
}