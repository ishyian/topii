package com.topiichat.app.core.presentation.navigation

import android.os.Bundle
import androidx.navigation.NavController

class Navigator(
    private val actionId: Int = 0,
    private val data: Bundle? = null,
    private val isBack: Boolean = false
) : INavigator {

    override fun navigate(navController: NavController) {
        if (isBack) {
            navigateBack(navController)
            return
        }
        if (data != null) {
            navigateWithData(navController)
            return
        }
        navController.navigate(actionId)
    }

    override fun navigateWithData(navController: NavController) {
        navController.navigate(actionId, data)
    }

    override fun navigateBack(navController: NavController) {
        navController.popBackStack()
    }
}




