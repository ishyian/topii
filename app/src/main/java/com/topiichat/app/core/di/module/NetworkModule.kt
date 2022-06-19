package com.topiichat.app.core.di.module

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.topiichat.app.BuildConfig
import com.topiichat.app.core.data.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesRetrofit(
        coroutineCallAdapterFactory: CoroutineCallAdapterFactory,
        converterFactory: Converter.Factory,
        okHttpClient: OkHttpClient,
    ): ApiService {
        return Retrofit.Builder()
            .baseUrl("")
            .addCallAdapterFactory(coroutineCallAdapterFactory)
            .addConverterFactory(converterFactory)
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun providesCoroutineCallAdapterFactory(): CoroutineCallAdapterFactory {
        return CoroutineCallAdapterFactory()
    }

    @Singleton
    @Provides
    fun providesMoshiConverterFactory(
        moshi: Moshi
    ): Converter.Factory {
        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesHttpLoggingInterceptor(
        logger: HttpLoggingInterceptor.Logger
    ): HttpLoggingInterceptor {
        val httpLoggingInterceptor = HttpLoggingInterceptor(logger)
        var level = HttpLoggingInterceptor.Level.NONE
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.BODY
        }
        httpLoggingInterceptor.level = level
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    fun providesLogger(): HttpLoggingInterceptor.Logger {
        return object : HttpLoggingInterceptor.Logger {
            override fun log(message: String) {
                Log.i("zm96", message)
            }
        }
    }
}