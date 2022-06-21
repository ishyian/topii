package com.topiichat.app.features.splash.presentation.model

sealed class Token {
    object Success : Token()
    class Fail(val msgError: String) : Token()
}