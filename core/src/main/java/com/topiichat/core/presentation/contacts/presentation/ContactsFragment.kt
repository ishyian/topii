package com.topiichat.core.presentation.contacts.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import com.topiichat.app.features.contacts.presentation.IContacsFragment
import com.topiichat.app.features.contacts.presentation.adapter.ContactsAdapter
import com.topiichat.app.features.contacts.presentation.adapter.delegates.ContactsSelectedAdapter
import com.topiichat.app.features.contacts.presentation.model.ContactsListUiModel
import com.topiichat.core.databinding.FragmentContactsBinding
import com.topiichat.core.delegates.parcelableParameters
import com.topiichat.core.extension.hideKeyboard
import com.topiichat.core.extension.lazyUnsynchronized
import com.topiichat.core.extension.viewModelCreator
import com.topiichat.core.presentation.platform.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactsFragment : BaseFragment<FragmentContactsBinding>(), IContacsFragment {

    @Inject
    lateinit var factory: ContactsViewModel.AssistedFactory
    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }
    private val parameters: ContactsParameters by parcelableParameters()

    private val contactsAdapter by lazyUnsynchronized {
        ContactsAdapter(onContactClick = viewModel::onContactClick)
    }

    private val contactsSelectedAdapter by lazyUnsynchronized {
        ContactsSelectedAdapter(onRemoveContactClick = viewModel::onRemoveContactClick)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentContactsBinding {
        return FragmentContactsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) = with(binding) {
        rvContacts.run {
            adapter = contactsAdapter
        }
        rvSelectedContacts.run {
            adapter = contactsSelectedAdapter
        }
        setupClickListener(toolbar.btnBack, textNext)
        editTextSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                viewModel.searchContacts(editTextSearch.text.toString())
                hideKeyboard()
                true
            } else false
        }
        initObservers()
        viewModel.loadContacts()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    private fun initObservers() = with(viewModel) {
        observe(content, ::onContentLoaded)
    }

    private fun hideKeyboard() {
        with(binding.editTextSearch) { post { hideKeyboard() } }
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = Unit

    override fun onContentLoaded(content: ContactsListUiModel) {
        contactsAdapter.items = content.items
        contactsSelectedAdapter.items = content.selectedItems
    }
}