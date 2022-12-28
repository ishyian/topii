package com.topiichat.app.features.home.data.repo

import com.topiichat.app.features.home.data.datasource.HomeRemoteDataSource
import com.topiichat.app.features.home.data.mapper.AvailableCountriesRemoteMapper
import com.topiichat.app.features.home.data.mapper.RecentUsersRemoteMapper
import com.topiichat.app.features.home.data.mapper.RemittanceHistoryRemoteMapper
import com.topiichat.app.features.home.domain.model.AvailableCountriesDomain
import com.topiichat.app.features.home.domain.model.RecentUserDomain
import com.topiichat.app.features.home.domain.model.RemittanceHistoryDomain
import com.topiichat.app.features.home.domain.repo.HomeRepository
import com.topiichat.core.coroutines.AppDispatchers
import com.topiichat.core.domain.ResultData
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(
    private val homeRemoteDataSource: HomeRemoteDataSource,
    private val remittanceHistoryMapper: RemittanceHistoryRemoteMapper,
    private val availableCountriesMapper: AvailableCountriesRemoteMapper,
    private val recentUsersMapper: RecentUsersRemoteMapper,
    private val appDispatchers: AppDispatchers
) : HomeRepository {
    override suspend fun getRemittanceHistory(
        token: String,
        year: Int,
        month: Int,
        senderId: String
    ): ResultData<RemittanceHistoryDomain> {
        return withContext(appDispatchers.network) {
            homeRemoteDataSource.getTransactionHistoryByMonth(
                token,
                year,
                month,
                senderId
            ).transformData {
                remittanceHistoryMapper.map(it)
            }
        }
    }

    override suspend fun getAvailableCountries(token: String): ResultData<AvailableCountriesDomain> {
        return withContext(appDispatchers.network) {
            homeRemoteDataSource.getAvailableCountries(token)
                .transformData {
                    availableCountriesMapper.map(it)
                }
        }
    }

    override suspend fun getRecentUsers(token: String): ResultData<List<RecentUserDomain>> {
        return withContext(appDispatchers.network) {
            homeRemoteDataSource.getRecentUsers(token)
                .transformData {
                    recentUsersMapper.map(it)
                }
        }
    }
}