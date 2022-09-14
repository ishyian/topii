package com.topiichat.app.features.pin_code.data.repo

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.pin_code.data.datasource.PinCodeRemoteDataSource
import com.topiichat.app.features.pin_code.data.mapper.ValidPinCodeRemoteMapper
import com.topiichat.app.features.pin_code.domain.model.ValidPinCodeDomain
import com.topiichat.app.features.pin_code.domain.repo.PinCodeRepository
import kotlinx.coroutines.withContext

class PinCodeRepositoryImpl(
    private val pinCodeRemoteDataSource: PinCodeRemoteDataSource,
    private val validPinCodeRemoteMapper: ValidPinCodeRemoteMapper,
    private val appDispatchers: AppDispatchers
) : PinCodeRepository {
    override suspend fun validatePinCode(pinCode: String): ResultData<ValidPinCodeDomain> {
        return withContext(appDispatchers.network) {
            pinCodeRemoteDataSource.validatePinCode(pinCode).transformData {
                validPinCodeRemoteMapper.map(it)
            }
        }
    }
}