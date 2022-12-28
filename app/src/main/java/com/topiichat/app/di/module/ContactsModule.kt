package com.topiichat.app.di.module

import android.content.Context
import com.topiichat.core.presentation.contacts.domain.model.ChatContactSelection
import contacts.core.Contacts
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ContactsModule {

    private val chatContactSelection = ChatContactSelection()

    @Provides
    fun provideContacts(
        @ApplicationContext context: Context
    ): Contacts {
        return Contacts(context)
    }

    @Provides
    fun providesChatContactSelection(): ChatContactSelection {
        return chatContactSelection
    }
}