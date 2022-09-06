package com.topiichat.app.features.contacts.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ContactsParameters(
    val isSingleSelection: Boolean = false
) : Parcelable