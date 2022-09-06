package com.topiichat.app.features.home.domain.repo

import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.home.domain.model.RemittanceHistoryDomain

interface HomeRepository {
    suspend fun getRemittanceHistory(
        token: String,
        year: Int,
        month: Int,
        senderId: String
    ): ResultData<RemittanceHistoryDomain>

    suspend fun getAvailableCountries(
        token: String
    ): ResultData<AvailableCountriesDomain>

    suspend fun getRecentUsers(
        token: String
    ): ResultData<List<RecentUserDomain>>
}