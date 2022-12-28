package com.topiichat.app.di.module

import com.topiichat.core.annotations.ChatNavigatorHolderQualifier
import com.topiichat.core.annotations.ChatRouterQualifier
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatNavigationModule {

    private val cicerone: Cicerone<Router> = Cicerone.create()

    @Singleton
    @Provides
    @ChatRouterQualifier
    fun provideRouter(): Router {
        return cicerone.router
    }

    @Singleton
    @Provides
    @ChatNavigatorHolderQualifier
    fun provideNavigatorHolder(): NavigatorHolder {
        return cicerone.navigatorHolder
    }
}