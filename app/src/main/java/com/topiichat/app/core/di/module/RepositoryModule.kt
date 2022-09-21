package com.topiichat.app.core.di.module

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.data.EmptyMapper
import com.topiichat.app.features.contacts.data.datasource.ContactsCacheDataStore
import com.topiichat.app.features.contacts.data.mapper.ContactsCacheMapper
import com.topiichat.app.features.contacts.data.repo.ContactsRepositoryImpl
import com.topiichat.app.features.contacts.domain.repo.ContactsRepository
import com.topiichat.app.features.home.data.datasource.HomeRemoteDataSource
import com.topiichat.app.features.home.data.mapper.AvailableCountriesRemoteMapper
import com.topiichat.app.features.home.data.mapper.RecentUsersRemoteMapper
import com.topiichat.app.features.home.data.mapper.RemittanceHistoryRemoteMapper
import com.topiichat.app.features.home.data.repo.HomeRepositoryImpl
import com.topiichat.app.features.home.domain.repo.HomeRepository
import com.topiichat.app.features.kyc.base.data.TokenAliceRemoteMapper
import com.topiichat.app.features.kyc.base.data.datasource.KYCRemoteDataSource
import com.topiichat.app.features.kyc.base.data.mapper.KYCStatusRemoteMapper
import com.topiichat.app.features.kyc.base.data.repo.KYCRepositoryImpl
import com.topiichat.app.features.kyc.base.domain.repo.KYCRepository
import com.topiichat.app.features.otp.data.datasource.OtpCodeRemoteDataSource
import com.topiichat.app.features.otp.data.mapper.ResendOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.mapper.ValidOtpCodeRemoteMapper
import com.topiichat.app.features.otp.data.repo.OtpCodeRepositoryImpl
import com.topiichat.app.features.otp.domain.repo.OtpCodeRepository
import com.topiichat.app.features.pin_code.data.datasource.PinCodeRemoteDataSource
import com.topiichat.app.features.pin_code.data.mapper.ValidPinCodeRemoteMapper
import com.topiichat.app.features.pin_code.data.repo.PinCodeRepositoryImpl
import com.topiichat.app.features.pin_code.domain.repo.PinCodeRepository
import com.topiichat.app.features.registration.data.datasource.cache.RegisterCacheDataStore
import com.topiichat.app.features.registration.data.datasource.remote.RegisterRemoteDataStore
import com.topiichat.app.features.registration.data.mapper.RegisterCacheMapper
import com.topiichat.app.features.registration.data.mapper.RegisterRemoteMapper
import com.topiichat.app.features.registration.data.repo.RegisterRepositoryImpl
import com.topiichat.app.features.registration.domain.repo.RegisterRepository
import com.topiichat.app.features.remittance.data.datasource.RemittanceRemoteDataSource
import com.topiichat.app.features.remittance.data.repo.RemittanceRepositoryImpl
import com.topiichat.app.features.remittance.domain.repo.RemittanceRepository
import com.topiichat.app.features.request_remittance.data.datasource.RequestRemittanceRemoteDataSource
import com.topiichat.app.features.request_remittance.data.mapper.RequestRemittanceRemoteMapper
import com.topiichat.app.features.request_remittance.data.repo.RequestRemittanceRepositoryImpl
import com.topiichat.app.features.request_remittance.domain.repo.RequestRemittanceRepository
import com.topiichat.app.features.send_remittance.data.datasource.SendRemittanceRemoteDataSource
import com.topiichat.app.features.send_remittance.data.mapper.CardsRemoteMapper
import com.topiichat.app.features.send_remittance.data.mapper.FxRateRemoteMapper
import com.topiichat.app.features.send_remittance.data.mapper.RemittancePurposesRemoteMapper
import com.topiichat.app.features.send_remittance.data.mapper.RemittanceRemoteMapper
import com.topiichat.app.features.send_remittance.data.repo.SendRemittanceRepositoryImpl
import com.topiichat.app.features.send_remittance.domain.repo.SendRemittanceRepository
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

    @Singleton
    @Provides
    fun providesContactsRepository(
        contactsCacheDataStore: ContactsCacheDataStore,
        contactsCacheMapper: ContactsCacheMapper,
        appDispatchers: AppDispatchers
    ): ContactsRepository {
        return ContactsRepositoryImpl(
            contactsCacheDataStore = contactsCacheDataStore,
            contactsCacheMapper = contactsCacheMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun providesHomeRepository(
        homeRemoteDataSource: HomeRemoteDataSource,
        remittanceHistoryMapper: RemittanceHistoryRemoteMapper,
        availableCountriesMapper: AvailableCountriesRemoteMapper,
        recentUsersMapper: RecentUsersRemoteMapper,
        appDispatchers: AppDispatchers
    ): HomeRepository {
        return HomeRepositoryImpl(
            homeRemoteDataSource = homeRemoteDataSource,
            remittanceHistoryMapper = remittanceHistoryMapper,
            availableCountriesMapper = availableCountriesMapper,
            recentUsersMapper = recentUsersMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun providesSendPaymentRepository(
        sendRemittanceRemoteDataSource: SendRemittanceRemoteDataSource,
        fxRateRemoteMapper: FxRateRemoteMapper,
        remittancePurposesRemoteMapper: RemittancePurposesRemoteMapper,
        cardsMapper: CardsRemoteMapper,
        remittanceRemoteMapper: RemittanceRemoteMapper,
        appDispatchers: AppDispatchers
    ): SendRemittanceRepository {
        return SendRemittanceRepositoryImpl(
            sendRemittanceRemoteDataSource = sendRemittanceRemoteDataSource,
            fxRateRemoteMapper = fxRateRemoteMapper,
            remittancePurposesRemoteMapper = remittancePurposesRemoteMapper,
            cardsMapper = cardsMapper,
            remittanceRemoteMapper = remittanceRemoteMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun providesKYCRepository(
        kycRemoteDataSource: KYCRemoteDataSource,
        kycStatusRemoteMapper: KYCStatusRemoteMapper,
        tokenAliceRemoteMapper: TokenAliceRemoteMapper,
        appDispatchers: AppDispatchers
    ): KYCRepository {
        return KYCRepositoryImpl(
            kycRemoteDataSource = kycRemoteDataSource,
            kycStatusRemoteMapper = kycStatusRemoteMapper,
            tokenAliceRemoteMapper = tokenAliceRemoteMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun providesRemittanceRepository(
        remittanceRemoteDataSource: RemittanceRemoteDataSource,
        remittanceRemoteMapper: RemittanceRemoteMapper,
        appDispatchers: AppDispatchers
    ): RemittanceRepository {
        return RemittanceRepositoryImpl(
            remittanceRemoteDataSource = remittanceRemoteDataSource,
            remittanceRemoteMapper = remittanceRemoteMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun providesPinCodeRepository(
        pinCodeRemoteDataSource: PinCodeRemoteDataSource,
        validPinCodeRemoteMapper: ValidPinCodeRemoteMapper,
        appDispatchers: AppDispatchers
    ): PinCodeRepository {
        return PinCodeRepositoryImpl(
            pinCodeRemoteDataSource = pinCodeRemoteDataSource,
            validPinCodeRemoteMapper = validPinCodeRemoteMapper,
            appDispatchers = appDispatchers
        )
    }

    @Singleton
    @Provides
    fun providesRequestRemittanceRepository(
        requestRemittanceDataSource: RequestRemittanceRemoteDataSource,
        requestRemittanceMapper: RequestRemittanceRemoteMapper,
        appDispatchers: AppDispatchers
    ): RequestRemittanceRepository {
        return RequestRemittanceRepositoryImpl(
            requestRemittanceDataSource = requestRemittanceDataSource,
            requestRemittanceMapper = requestRemittanceMapper,
            appDispatchers = appDispatchers
        )
    }
}