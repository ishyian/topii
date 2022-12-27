package com.topiichat.core.presentation.contacts.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactsParameters(
    val isChatSelectContact: Boolean = false,
    val isSingleSelection: Boolean = false
) : Parcelable