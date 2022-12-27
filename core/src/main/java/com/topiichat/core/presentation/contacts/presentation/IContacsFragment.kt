package com.topiichat.app.features.contacts.presentation

import com.topiichat.app.features.contacts.presentation.model.ContactsListUiModel
import com.topiichat.core.presentation.platform.IBaseFragment

interface IContacsFragment : IBaseFragment {
    fun onContentLoaded(content: ContactsListUiModel)
}