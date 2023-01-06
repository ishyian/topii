package com.topiichat.remittance.features.new_beneficiary.data.repo

import com.topiichat.core.coroutines.AppDispatchers
import com.topiichat.core.domain.ResultData
import com.topiichat.remittance.features.new_beneficiary.data.datasource.BeneficiaryRemoteDataSource
import com.topiichat.remittance.features.new_beneficiary.data.mapper.BeneficiaryCountriesListMapper
import com.topiichat.remittance.features.new_beneficiary.data.mapper.BeneficiaryDocumentTypesListMapper
import com.topiichat.remittance.features.new_beneficiary.data.mapper.BeneficiaryProductTypesListMapper
import com.topiichat.remittance.features.new_beneficiary.domain.model.CountryDomain
import com.topiichat.remittance.features.new_beneficiary.domain.model.DocumentTypeDomain
import com.topiichat.remittance.features.new_beneficiary.domain.model.ProductDomain
import com.topiichat.remittance.features.new_beneficiary.domain.repo.BeneficiaryRepository
import kotlinx.coroutines.withContext

class BeneficiaryRepositoryImpl(
    private val dataSource: BeneficiaryRemoteDataSource,
    private val countriesMapper: BeneficiaryCountriesListMapper,
    private val productTypesMapper: BeneficiaryProductTypesListMapper,
    private val documentTypesMapper: BeneficiaryDocumentTypesListMapper,
    private val appDispatchers: AppDispatchers
) : BeneficiaryRepository {
    override suspend fun getCountriesList(accessToken: String): ResultData<List<CountryDomain>> {
        return withContext(appDispatchers.network) {
            dataSource.getBeneficiaryCountriesList(accessToken).transformData {
                countriesMapper.map(it)
            }
        }
    }

    override suspend fun getProductTypes(accessToken: String): ResultData<List<ProductDomain>> {
        return withContext(appDispatchers.network) {
            dataSource.getBeneficiaryProductTypesList(accessToken).transformData {
                productTypesMapper.map(it)
            }
        }
    }

    override suspend fun getDocumentTypes(accessToken: String): ResultData<List<DocumentTypeDomain>> {
        return withContext(appDispatchers.network) {
            dataSource.getBeneficiaryDocumentTypesList(accessToken).transformData {
                documentTypesMapper.map(it)
            }
        }
    }
}