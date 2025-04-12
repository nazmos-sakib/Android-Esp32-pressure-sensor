package com.example.android_esp32_presure_sensore_esp_now.data

import androidx.navigation.NavHostController
import com.example.android_esp32_presure_sensore_esp_now.domain.AppState

class AppStateImpl(private val _navController: NavHostController) : AppState {

    override val navController: NavHostController
        get() = _navController


    override fun popUp() {
        navController.popBackStack()
    }

    override fun navigate(route: String) {
        navController.navigate(route) { launchSingleTop = true }
    }

    override fun navigateAndPopUp(route: String, popUp: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(popUp) { inclusive = true }
        }    }

    override fun clearAndNavigate(route: String) {
        navController.navigate(route) {
            launchSingleTop = true
            popUpTo(0) { inclusive = true }
        }
    }

}