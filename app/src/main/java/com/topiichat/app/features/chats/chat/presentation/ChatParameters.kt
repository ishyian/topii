package com.topiichat.app.features.chats.chat.presentation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChatParameters(
    val from: String
) : Parcelable