package com.topiichat.app.features.send_remittance.data.mapper

import com.topiichat.app.features.send_remittance.data.model.RemittancePurposeDto
import com.topiichat.app.features.send_remittance.domain.model.RemittancePurposeDomain
import com.topiichat.core.domain.Mapper
import javax.inject.Inject

class RemittancePurposesRemoteMapper @Inject constructor() : Mapper<List<RemittancePurposeDto>,
    List<RemittancePurposeDomain>?> {
    override fun map(input: List<RemittancePurposeDto>?): List<RemittancePurposeDomain> {
        return input?.map {
            RemittancePurposeDomain(
                label = it.label,
                value = it.value
            )
        } ?: emptyList()
    }
}