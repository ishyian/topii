package com.topiichat.app.features.contacts.presentation.model

data class ContactsListUiModel(
    val items: List<Any>
) {
    val selectedItems: List<ContactUiModel>
        get() = items.filterIsInstance(ContactUiModel::class.java)
            .filter { contact -> contact.isSelected }
}

fun ContactsListUiModel.changeContactCheckedStatus(
    contact: ContactUiModel
): ContactsListUiModel {
    return ContactsListUiModel(
        items.map { item ->
            when {
                item is ContactUiModel && item.telephone == contact.telephone -> {
                    item.copy(isSelected = !item.isSelected)
                }
                else -> {
                    item
                }
            }
        }
    )
}
