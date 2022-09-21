package com.topiichat.app.features.kyc.email.data

interface AliceSdkListener {
    fun onVerificationSuccess(userId: String)
}