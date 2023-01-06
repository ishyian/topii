package com.topiichat.remittance.data

import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryCountryDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryDocumentTypeDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryInstitutionDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryProductTypeDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryValidUserDto
import com.topiichat.remittance.features.new_beneficiary.data.model.BeneficiaryValidUserRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface RemittanceApiService {
    @GET("api/v1/beneficiary/list/country/")
    suspend fun getBeneficiaryCountriesList(@Header("Authorization") accessToken: String): List<BeneficiaryCountryDto>

    @GET("api/v1/beneficiary/list/institutions/")
    suspend fun getBeneficiaryInstitutionsList(
        @Header("Authorization") accessToken: String,
        @Query("country_id") countryId: Int
    ): List<BeneficiaryInstitutionDto>

    @GET("api/v1/beneficiary/list/product/type/")
    suspend fun getBeneficiaryProductTypesList(
        @Header("Authorization") accessToken: String
    ): List<BeneficiaryProductTypeDto>

    @GET("api/v1/beneficiary/list/document/type/")
    suspend fun getBeneficiaryDocumentTypesList(
        @Header("Authorization") accessToken: String
    ): List<BeneficiaryDocumentTypeDto>

    @POST("api/v1/beneficiary/valid/user/")
    suspend fun validateBeneficiaryUser(
        @Header("Authorization") accessToken: String,
        @Body validateUserRequestDto: BeneficiaryValidUserRequestDto
    ): BeneficiaryValidUserDto
}