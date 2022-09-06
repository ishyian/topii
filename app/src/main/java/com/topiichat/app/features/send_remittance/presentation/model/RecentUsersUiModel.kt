package com.topiichat.app.features.send_remittance.presentation.model

data class RecentUsersUiModel(
    val items: List<Any>
)

fun RecentUsersUiModel.changeCheckedStatus(
    recentUser: RecentUserUiModel
): RecentUsersUiModel {
    return RecentUsersUiModel(
        items.map { item ->
            when {
                item is RecentUserUiModel &&
                    item.data.recipientId == recentUser.data.recipientId -> {
                    item.copy(isSelected = !item.isSelected)
                }
                item is RecentUserUiModel &&
                    item.data.recipientId != recentUser.data.recipientId -> {
                    item.copy(isSelected = false)
                }
                else -> {
                    item
                }
            }
        }
    )
}