package com.topiichat.app.features.splash.data.repo

import com.topiichat.app.core.data.EmptyMapper
import com.topiichat.app.core.domain.EmptyDomain
import com.topiichat.app.core.domain.ResultData
import com.topiichat.app.features.splash.data.datasource.cache.AuthCacheDataStore
import com.topiichat.app.features.splash.data.datasource.remote.AuthRemoteDataStore
import com.topiichat.app.features.splash.data.mapper.TokenCacheMapper
import com.topiichat.app.features.splash.data.mapper.TokenRemoteMapper
import com.topiichat.app.features.splash.domain.model.TokenDomain
import com.topiichat.app.features.splash.domain.repo.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authRemoteDataStore: AuthRemoteDataStore,
    private val authCacheDataStore: AuthCacheDataStore,
    private val emptyMapper: EmptyMapper,
    private val tokenRemoteMapper: TokenRemoteMapper,
    private val tokenCacheMapper: TokenCacheMapper
) : AuthRepository {

    override suspend fun fetchTokenRemote(): ResultData<TokenDomain> {
        return authRemoteDataStore.fetchToken().transformData {
            tokenRemoteMapper.map(it)
        }
    }

    override suspend fun fetchTokenCache(): ResultData<TokenDomain> {
        return authCacheDataStore.fetchToken().transformData {
            tokenCacheMapper.map(it)
        }
    }

    override suspend fun saveToken(token: TokenDomain): ResultData<EmptyDomain> {
        return authCacheDataStore.saveToken(token).transformData {
            emptyMapper.map(it)
        }
    }
}