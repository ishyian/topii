package com.topiichat.app.features.valid_phone_number.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.valid_phone_number.data.datasource.ValidPhoneRemoteDataStore
import com.topiichat.app.features.valid_phone_number.data.mapper.VerifyPhoneRemoteMapper
import com.topiichat.app.features.valid_phone_number.domain.model.VerifyPhoneDomain
import com.topiichat.app.features.valid_phone_number.domain.repo.ValidPhoneRepository
import kotlinx.coroutines.withContext

class ValidPhoneRepositoryImpl(
    private val validPhoneRemoteDataStore: ValidPhoneRemoteDataStore,
    private val verifyPhoneRemoteMapper: VerifyPhoneRemoteMapper,
    private val appDispatchers: AppDispatchers
) : ValidPhoneRepository {

    override suspend fun verifyPhone(
        phoneNumber: String,
        code: String,
        isoCode: String
    ): ResultData<VerifyPhoneDomain> {
        return withContext(appDispatchers.network) {
            validPhoneRemoteDataStore.verifyPhoneNumber(phoneNumber, code, isoCode).transformData {
                verifyPhoneRemoteMapper.map(it)
            }
        }
    }
}