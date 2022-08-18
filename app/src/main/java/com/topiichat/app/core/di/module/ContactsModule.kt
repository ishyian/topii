package com.topiichat.app.core.di.module

import android.content.Context
import contacts.core.Contacts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContactsModule {

    @Provides
    fun provideContacts(
        @ApplicationContext context: Context
    ): Contacts {
        return Contacts(context)
    }
}