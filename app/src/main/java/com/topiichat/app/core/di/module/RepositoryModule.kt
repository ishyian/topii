package com.topiichat.app.core.di.module

import com.topiichat.app.core.data.EmptyMapper
import com.topiichat.app.features.splash.data.datasource.cache.AuthCache
import com.topiichat.app.features.splash.data.datasource.cache.AuthCacheDataStore
import com.topiichat.app.features.splash.data.datasource.cache.AuthCacheImpl
import com.topiichat.app.features.splash.data.datasource.remote.AuthRemoteDataStore
import com.topiichat.app.features.splash.data.mapper.TokenCacheMapper
import com.topiichat.app.features.splash.data.mapper.TokenRemoteMapper
import com.topiichat.app.features.splash.data.repo.AuthRepositoryImpl
import com.topiichat.app.features.splash.domain.repo.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthRepository(
        authRemoteDataStore: AuthRemoteDataStore,
        authCacheDataStore: AuthCacheDataStore,
        emptyMapper: EmptyMapper,
        tokenRemoteMapper: TokenRemoteMapper,
        tokenCacheMapper: TokenCacheMapper
    ): AuthRepository {
        return AuthRepositoryImpl(
            authRemoteDataStore = authRemoteDataStore,
            authCacheDataStore = authCacheDataStore,
            emptyMapper = emptyMapper,
            tokenRemoteMapper = tokenRemoteMapper,
            tokenCacheMapper = tokenCacheMapper
        )
    }
}