package com.topiichat.app.core.di.module

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.data.EmptyMapper
import com.topiichat.app.features.otp.data.datasource.OtpCodeRemoteDataSource
import com.topiichat.app.features.otp.data.mapper.ResendOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.mapper.ValidOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.repo.OtpCodeRepositoryImpl
import com.topiichat.app.features.otp.domain.repo.OtpCodeRepository
import com.topiichat.app.features.registration.data.datasource.cache.RegisterCacheDataStore
import com.topiichat.app.features.registration.data.datasource.remote.RegisterRemoteDataStore
import com.topiichat.app.features.registration.data.mapper.RegisterCacheMapper
import com.topiichat.app.features.registration.data.mapper.RegisterRemoteMapper
import com.topiichat.app.features.registration.data.repo.RegisterRepositoryImpl
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import com.topiichat.app.features.splash.data.datasource.cache.AuthCacheDataStore
import com.topiichat.app.features.splash.data.datasource.remote.AuthRemoteDataStore
import com.topiichat.app.features.splash.data.mapper.TokenCacheMapper
import com.topiichat.app.features.splash.data.mapper.TokenRemoteMapper
import com.topiichat.app.features.splash.data.repo.AuthRepositoryImpl
import com.topiichat.app.features.splash.domain.repo.AuthRepository
import com.topiichat.app.features.valid_phone_number.data.datasource.ValidPhoneRemoteDataStore
import com.topiichat.app.features.valid_phone_number.data.mapper.VerifyPhoneRemoteMapper
import com.topiichat.app.features.valid_phone_number.data.repo.ValidPhoneRepositoryImpl
import com.topiichat.app.features.valid_phone_number.domain.repo.ValidPhoneRepository
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

    @Singleton
    @Provides
    fun provideValidPhoneRepository(
        validPhoneRemoteDataStore: ValidPhoneRemoteDataStore,
        verifyPhoneRemoteMapper: VerifyPhoneRemoteMapper,
        appDispatchers: AppDispatchers
    ): ValidPhoneRepository {
        return ValidPhoneRepositoryImpl(
            validPhoneRemoteDataStore = validPhoneRemoteDataStore,
            verifyPhoneRemoteMapper = verifyPhoneRemoteMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun provideOtpCodeRepository(
        otpCodeRemoteDataSource: OtpCodeRemoteDataSource,
        validOtpCodeRemoteMapper: ValidOtpCodeRemoteMapper,
        resendOtpCodeRemoteMapper: ResendOtpCodeRemoteMapper,
        appDispatchers: AppDispatchers
    ): OtpCodeRepository {
        return OtpCodeRepositoryImpl(
            otpCodeRemoteDataSource = otpCodeRemoteDataSource,
            validOtpCodeRemoteMapper = validOtpCodeRemoteMapper,
            resendOtpCodeRemoteMapper = resendOtpCodeRemoteMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun provideRegisterRepository(
        registerCacheDataStore: RegisterCacheDataStore,
        registerRemoteDataStore: RegisterRemoteDataStore,
        registerRemoteMapper: RegisterRemoteMapper,
        registerCacheMapper: RegisterCacheMapper,
        emptyMapper: EmptyMapper,
        appDispatchers: AppDispatchers
    ): RegisterRepository {
        return RegisterRepositoryImpl(
            registerCacheDataStore = registerCacheDataStore,
            registerRemoteDataStore = registerRemoteDataStore,
            registerRemoteMapper = registerRemoteMapper,
            registerCacheMapper = registerCacheMapper,
            emptyMapper = emptyMapper,
            appDispatchers = appDispatchers
        )
    }
}