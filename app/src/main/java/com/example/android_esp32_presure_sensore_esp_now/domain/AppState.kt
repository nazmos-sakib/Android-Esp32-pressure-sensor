package com.example.android_esp32_presure_sensore_esp_now.domain

import androidx.navigation.NavHostController

interface AppState {
    val navController: NavHostController

    fun popUp()

    fun navigate(route: String)

    fun navigateAndPopUp(route: String, popUp: String)

    fun clearAndNavigate(route: String)
}