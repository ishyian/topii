package com.topiichat.app.features.request_remittance.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.bumptech.glide.Glide
import com.topiichat.app.AppActivity
import com.topiichat.app.core.constants.Constants.INITIAL_COUNTRY_ISO_CODE
import com.topiichat.app.core.delegates.parcelableParameters
import com.topiichat.app.core.extension.addStartCircleDrawableFromUrl
import com.topiichat.app.core.extension.getPhoneNumber
import com.topiichat.app.core.extension.lazyUnsynchronized
import com.topiichat.app.core.extension.showDropDownWhenClick
import com.topiichat.app.core.extension.viewModelCreator
import com.topiichat.app.core.presentation.platform.BaseFragment
import com.topiichat.app.databinding.FragmentRequestRemittanceBinding
import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.model.CountryDomain
import com.topiichat.app.features.request_remittance.presentation.model.BtnRequestEnablingUi
import com.topiichat.app.features.request_remittance.presentation.model.SenderUiModel
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.app.features.send_remittance.presentation.adapter.RecentUsersAdapter
import com.topiichat.app.features.send_remittance.presentation.model.RecentUsersUiModel
import dagger.hilt.android.AndroidEntryPoint
import me.ibrahimsn.lib.PhoneNumberKit
import javax.inject.Inject

@AndroidEntryPoint
class RequestRemittanceFragment : BaseFragment<FragmentRequestRemittanceBinding>(),
    IRequestRemittanceFragment,
    CompoundButton.OnCheckedChangeListener,
    AdapterView.OnItemSelectedListener {

    @Inject
    lateinit var factory: RequestRemittanceViewModel.AssistedFactory

    private val viewModel by viewModelCreator {
        factory.create(parameters)
    }

    private val parameters: RequestRemittanceParameters by parcelableParameters()

    private val recentUsersAdapter by lazyUnsynchronized {
        RecentUsersAdapter(
            onUserClick = viewModel::onRecentUserClick,
            onAddUserClick = viewModel::onAddUserClick
        )
    }

    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentRequestRemittanceBinding.inflate(inflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        rvRecentUsers.run {
            adapter = recentUsersAdapter
        }
        switchPayment.setOnCheckedChangeListener(this@RequestRemittanceFragment)
        editTextRemittanceConcept.onItemSelectedListener = this@RequestRemittanceFragment
        editTextNotes.doAfterTextChanged { notesText ->
            notesText?.let { viewModel.onDescriptionChanged(it.toString()) }
        }
        cardSenderContactData.editText.doAfterTextChanged { phoneNumber ->
            phoneNumber?.let {
                viewModel.onPhoneNumberChanged(
                    cardSenderContactData.editText.getPhoneNumber(
                        requireContext()
                    )
                )
            }
        }
        cardRequestPayment.editInputSum.doAfterTextChanged { text ->
            if (text != null && text.isNotEmpty()) {
                viewModel.onAmountChanged(text.toString().toDouble())
            } else viewModel.onAmountChanged(0.0)
        }
        setupClickListener(toolbar.btnBack, btnRequest)
        initObservers()
    }

    override fun onVisibilityLoader(isVisibleLoader: Boolean) = with(binding) {
        groupContent.isVisible = isVisibleLoader.not()
        progressBar.isVisible = isVisibleLoader
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        viewModel.onCheckedChanged(buttonView?.id, isChecked)
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.onPurposeSelected(position)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        viewModel.onPurposeNotSelected()
    }

    override fun onClick(v: View?) {
        viewModel.onClick(v)
    }

    override fun onEnablingBtnSend(
        btnRequestEnablingUi: BtnRequestEnablingUi
    ) = with(binding.btnRequest) {
        isEnabled = btnRequestEnablingUi.isEnabled
        setBackgroundResource(btnRequestEnablingUi.backgroundId)
    }

    override fun onRecentUsersLoaded(recentUsersModel: RecentUsersUiModel) {
        recentUsersAdapter.items = recentUsersModel.items
    }

    override fun onRemittancePurposesLoaded(purposes: List<RemittancePurposeDomain>?) = with(binding) {
        val items = purposes?.map { purpose -> purpose.label } ?: emptyList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, items)
        editTextRemittanceConcept.apply {
            setText(items.firstOrNull() ?: "")
            setAdapter(adapter)
            threshold = 0
            showDropDownWhenClick()
        }
    }

    override fun onReceiverCountryChanged(receiverCountry: CountryDomain) = with(binding) {
        cardRequestPayment.textRequestCurrencyPicker.text = receiverCountry.currencyCode
        cardRequestPayment.textRequestCurrencyPicker.addStartCircleDrawableFromUrl(receiverCountry.flagImageUrl)
    }

    private fun onShowContactDataInput(ignore: Unit) = with(binding) {
        textDestinationTitle.isVisible = false
        rvRecentUsers.isVisible = false
        cardSenderContactData.root.isVisible = true
    }

    private fun onAvailableCountriesLoaded(data: AvailableCountriesDomain) {
        val admitCountries = data.countries.map { it.code.lowercase() }
        val phoneNumberKit = PhoneNumberKit.Builder(requireContext())
            .setIconEnabled(true)
            .admitCountries(admitCountries)
            .build()

        phoneNumberKit.attachToInput(
            binding.cardSenderContactData.layoutEditText,
            admitCountries.firstOrNull() ?: INITIAL_COUNTRY_ISO_CODE
        )
        phoneNumberKit.setupCountryPicker(
            activity = requireActivity() as AppActivity,
            searchEnabled = true
        )
    }

    override fun onSenderSelected(senderUiModel: SenderUiModel) = with(binding) {
        Glide.with(requireContext())
            .load(senderUiModel.userData.avatar)
            .circleCrop()
            .into(layoutSender.imageAvatar)

        layoutSender.textRecipientName.text = senderUiModel.userData.fullName
        layoutSender.textRecipientCountry.text = senderUiModel.senderCountry?.name ?: ""
        layoutSender.textRecipientCountry.addStartCircleDrawableFromUrl(
            senderUiModel.senderCountry?.flagImageUrl ?: ""
        )

        textDestinationTitle.isVisible = false
        rvRecentUsers.isVisible = false
        layoutSender.root.isVisible = true
    }

    private fun initObservers() = with(viewModel) {
        observe(showMsgError, ::showErrorMessage)
        observe(showLoader, ::onVisibilityLoader)
        observe(receiverCountry, ::onReceiverCountryChanged)
        observe(remittancePurposes, ::onRemittancePurposesLoaded)
        observe(recentUsers, ::onRecentUsersLoaded)
        observe(btnRequestEnabling, ::onEnablingBtnSend)
        observe(showContactDataInput, ::onShowContactDataInput)
        observe(availableCountries, ::onAvailableCountriesLoaded)
        observe(senderModel, ::onSenderSelected)
    }
}