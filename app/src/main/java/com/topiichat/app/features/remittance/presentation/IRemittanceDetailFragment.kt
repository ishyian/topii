package com.topiichat.app.features.remittance.presentation

import com.topiichat.app.features.send_remittance.domain.model.RemittanceDomain
import com.topiichat.core.presentation.platform.IBaseFragment

interface IRemittanceDetailFragment : IBaseFragment {
    fun onRemittanceDetailLoaded(remittance: RemittanceDomain)
}