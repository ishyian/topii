package com.topiichat.app.core.presentation.navigation

import androidx.navigation.NavController

interface INavigator {
    fun navigate(navController: NavController)
    fun navigateWithData(navController: NavController)
    fun navigateBack(navController: NavController)
}