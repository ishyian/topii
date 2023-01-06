package com.topiichat.remittance.features.new_beneficiary.domain.repo

import com.topiichat.core.domain.ResultData
import com.topiichat.remittance.features.new_beneficiary.domain.model.CountryDomain
import com.topiichat.remittance.features.new_beneficiary.domain.model.DocumentTypeDomain
import com.topiichat.remittance.features.new_beneficiary.domain.model.ProductDomain

interface BeneficiaryRepository {
    suspend fun getCountriesList(accessToken: String): ResultData<List<CountryDomain>>
    suspend fun getProductTypes(accessToken: String): ResultData<List<ProductDomain>>
    suspend fun getDocumentTypes(accessToken: String): ResultData<List<DocumentTypeDomain>>
}