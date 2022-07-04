package com.topiichat.app.core.di

import com.topiichat.app.core.coroutines.AppDispatchers
import com.topiichat.app.core.coroutines.JvmAppDispatchers
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
