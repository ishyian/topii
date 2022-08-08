package com.topiichat.app.core.di.module

import android.content.Context
import android.content.SharedPreferences
import com.topiichat.app.features.registration.data.datasource.cache.RegisterCache
import com.topiichat.app.features.registration.data.datasource.cache.RegisterCacheImpl
import com.topiichat.app.features.splash.data.datasource.cache.AuthCache
import com.topiichat.app.features.splash.data.datasource.cache.AuthCacheImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    private const val APP_SHARED_PREF = "TopiiChatSharedPref"

    @Provides
    fun provideSharedPreferences(
        @ApplicationContext context: Context,
    ): SharedPreferences {
        return context.getSharedPreferences(APP_SHARED_PREF, Context.MODE_PRIVATE)
    }

    @Provides
    fun providesAuthCache(
        pref: SharedPreferences
    ): AuthCache {
        return AuthCacheImpl(pref)
    }

    @Provides
    fun providesRegisterCache(
        pref: SharedPreferences
    ): RegisterCache {
        return RegisterCacheImpl(pref)
    }
}