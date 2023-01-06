package com.topiichat.remittance.features.new_beneficiary.data.mapper

import com.topiichat.core.domain.Mapper
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryDocumentTypeDto
import com.topiichat.remittance.features.new_beneficiary.domain.model.DocumentTypeDomain
import javax.inject.Inject

class BeneficiaryDocumentTypesListMapper @Inject constructor() :
    Mapper<List<BeneficiaryDocumentTypeDto>, List<DocumentTypeDomain>> {
    override fun map(input: List<BeneficiaryDocumentTypeDto>?): List<DocumentTypeDomain> {
        return input?.let { unboxedList ->
            unboxedList.map { document ->
                DocumentTypeDomain(
                    id = document.id.toInt(),
                    name = document.name,
                    value = document.value
                )
            }
        } ?: emptyList()
    }
}