package com.topiichat.remittance.features.new_beneficiary.data.mapper

import com.topiichat.core.domain.Mapper
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryProductTypeDto
import com.topiichat.remittance.features.new_beneficiary.domain.model.ProductDomain
import javax.inject.Inject

class BeneficiaryProductTypesListMapper @Inject constructor() :
    Mapper<List<BeneficiaryProductTypeDto>, List<ProductDomain>> {
    override fun map(input: List<BeneficiaryProductTypeDto>?): List<ProductDomain> {
        return input?.let { unboxedList ->
            unboxedList.map { product ->
                ProductDomain(
                    id = product.id.toInt(),
                    name = product.name,
                    value = product.value
                )
            }
        } ?: emptyList()
    }
}