package com.topiichat.app.features.home.data.datasource

import com.topiichat.app.core.data.ApiService
import com.topiichat.app.core.data.datasource.BaseRemoteDataStore
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.home.data.model.AvailableCountryDto
import com.topiichat.app.features.home.data.model.RecentUsersDto
import com.topiichat.app.features.home.data.model.RemittanceHistoryDto
import com.topiichat.app.features.home.data.model.RemittanceHistoryRequestDto
import javax.inject.Inject

class HomeRemoteDataSource @Inject constructor(
    private val apiService: ApiService
) : BaseRemoteDataStore() {
    suspend fun getTransactionHistoryByMonth(
        token: String,
        year: Int,
        month: Int,
        senderId: String,
        skip: Int = 0,
        first: Int = 0
    ): ResultData<RemittanceHistoryDto?> {
        return safeApiCall {
            apiService.getRemittanceHistory(
                token, RemittanceHistoryRequestDto(
                    year = year,
                    month = month,
                    senderId = senderId,
                    skip = skip,
                    first = first
                )
            )
        }
    }

    suspend fun getAvailableCountries(token: String): ResultData<List<AvailableCountryDto>?> {
        return safeApiCall {
            apiService.getAvailableCountries(
                accessToken = token
            )
        }
    }

    suspend fun getRecentUsers(token: String): ResultData<RecentUsersDto?> {
        return safeApiCall {
            apiService.getRecentRemittances(
                accessToken = token
            )
        }
    }
}