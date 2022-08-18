package com.topiichat.app.features.contacts.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.contacts.presentation.model.ContactsListUiModel

interface IContacsFragment : IBaseFragment {
    fun onContentLoaded(content: ContactsListUiModel)
}