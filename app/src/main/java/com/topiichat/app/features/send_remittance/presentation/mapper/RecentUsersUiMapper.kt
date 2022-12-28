package com.topiichat.app.features.send_remittance.presentation.mapper

import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.send_remittance.presentation.model.RecentAddUserUiModel
import com.topiichat.app.features.send_remittance.presentation.model.RecentUserUiModel
import com.topiichat.app.features.send_remittance.presentation.model.RecentUsersUiModel
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class RecentUsersUiMapper @Inject constructor() : Mapper<List<RecentUserDomain>,
    RecentUsersUiModel> {
    override fun map(input: List<RecentUserDomain>?): RecentUsersUiModel {
        val uiModels = mutableListOf<Any>(RecentAddUserUiModel)
        input?.let { recentRemittances ->
            uiModels.addAll(recentRemittances.map { RecentUserUiModel(it) })
        }
        return RecentUsersUiModel(items = uiModels)
    }
}