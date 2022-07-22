package com.topiichat.app.features.kyc.document.presentation

import com.topiichat.app.features.kyc.base.presentation.IKYCViewModel

interface IDocumentViewModel : IKYCViewModel {
    fun onDocumentChanged(value: String)
}