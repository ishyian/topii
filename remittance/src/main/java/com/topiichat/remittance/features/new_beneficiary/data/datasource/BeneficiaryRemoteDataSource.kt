package com.topiichat.remittance.features.new_beneficiary.data.datasource

import com.topiichat.core.data.datasource.BaseRemoteDataStore
import com.topiichat.core.domain.ResultData
import com.topiichat.remittance.data.RemittanceApiService
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryCountryDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryDocumentTypeDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryInstitutionDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryProductTypeDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryValidUserDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryValidUserRequestDto
import javax.inject.Inject

class BeneficiaryRemoteDataSource @Inject constructor(
    private val apiService: RemittanceApiService
) : BaseRemoteDataStore() {

    suspend fun getBeneficiaryCountriesList(
        accessToken: String
    ): ResultData<List<BeneficiaryCountryDto>> {
        return safeApiCall {
            apiService.getBeneficiaryCountriesList(accessToken)
        }
    }

    suspend fun getBeneficiaryProductTypesList(
        accessToken: String
    ): ResultData<List<BeneficiaryProductTypeDto>> {
        return safeApiCall {
            apiService.getBeneficiaryProductTypesList(accessToken)
        }
    }

    suspend fun getBeneficiaryDocumentTypesList(
        accessToken: String
    ): ResultData<List<BeneficiaryDocumentTypeDto>> {
        return safeApiCall {
            apiService.getBeneficiaryDocumentTypesList((accessToken))
        }
    }

    suspend fun getBeneficiaryInstitutionsList(
        accessToken: String,
        countryId: Int
    ): ResultData<List<BeneficiaryInstitutionDto>> {
        return safeApiCall {
            apiService.getBeneficiaryInstitutionsList(accessToken, countryId)
        }
    }

    suspend fun validateBeneficiaryUser(
        accessToken: String,
        dialCountryCode: String,
        phoneNumber: String
    ): ResultData<BeneficiaryValidUserDto> {
        return safeApiCall {
            apiService.validateBeneficiaryUser(
                accessToken = accessToken,
                validateUserRequestDto = BeneficiaryValidUserRequestDto(
                    dialCountryCode = dialCountryCode,
                    mobileNumber = phoneNumber
                )
            )
        }
    }
}