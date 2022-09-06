package com.topiichat.app.features.remittance.presentation

import com.topiichat.app.core.presentation.platform.IBaseFragment
import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain

interface IRemittanceDetailFragment : IBaseFragment {
    fun onRemittanceDetailLoaded(remittance: RemittanceDomain)
}