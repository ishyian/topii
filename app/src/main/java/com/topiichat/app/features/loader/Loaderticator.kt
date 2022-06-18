package com.topiichat.app.features.loader

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Loaderticator
@Inject constructor() {
    //Learning purpose: We assume the user is always logged in
    //Here you should put your own logic to return whether the user
    //is authenticated or not
    fun loadingDone() = true
}