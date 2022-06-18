package com.topiichat.app.core.navigation

import android.os.Bundle
import androidx.navigation.NavController

class Navigator(
    private val actionId: Int = 0,
    private val data: Bundle? = null,
    private val isBack: Boolean = false
) {
    fun navigate(navController: NavController) {
        if (isBack) {
            navController.popBackStack()
            return
        }
        navController.navigate(actionId)
    }

    fun navigateWithData(navController: NavController) {
        navController.navigate(actionId, data)
    }
}




