package com.topiichat.app.di

import com.topiichat.core.coroutines.AppDispatchers
import com.topiichat.core.coroutines.JvmAppDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Singleton
    @Provides
    fun provideAppDispatchers(): AppDispatchers {
        return JvmAppDispatchers()
    }
}
