package com.example.android_esp32_presure_sensore_esp_now.domain

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_esp32_presure_sensore_esp_now.presentation.BluetoothUiState
import com.example.android_esp32_presure_sensore_esp_now.presentation.BluetoothViewModel
import com.example.android_esp32_presure_sensore_esp_now.presentation.screens.info.Info
import com.example.android_esp32_presure_sensore_esp_now.presentation.screens.pair_new_devices.PairNewDevice
import com.example.android_esp32_presure_sensore_esp_now.presentation.screens.saved_devices.SavedDevices
import com.example.android_esp32_presure_sensore_esp_now.presentation.screens.settings.Settings
import com.example.android_esp32_presure_sensore_esp_now.presentation.screens.terminal.Terminal
import com.example.android_esp32_presure_sensore_esp_now.data.AppStateImpl
import com.example.android_esp32_presure_sensore_esp_now.presentation.screens.history.History


@Composable
fun rememberAppState(navController: NavHostController = rememberNavController()) =
    remember(navController) {
        AppStateImpl(navController)
    }


//extended function of navHost
fun NavGraphBuilder.routsGraph(
    appState:  AppState,
    uiState: BluetoothUiState,
    viewModel: BluetoothViewModel
) {

    when{
        uiState.isConnected ->{
            appState.navigate(ROUTE_TERMINAL)
        }
    }

    composable(ROUTE_TERMINAL){
        //HomeScreen(onClickGotoBluetoothScreen = { route -> appState.navigate (route) })
        Terminal(
            state = uiState,
            onDisconnect = viewModel::disconnectFromDevice,
            onSessionStart = viewModel::startAWorkoutSession,
            onSessionEnd = viewModel::endWorkoutSession
        )
    }

    composable(ROUTE_HISTORY){
        //by triggering aktual the list
        viewModel.updateSessionIdOccurrences()
        History(
            state = uiState,
            onDetailsSession =  viewModel::updateSessionDetailsBySessionId
        )
    }
    composable(ROUTE_PAIR_NEW_DEVICE){
        //HomeScreen(onClickGotoBluetoothScreen = { route -> appState.navigate (route) })
        PairNewDevice(
            scannedDevices = uiState.scannedDevices,
            //onDeviceClick = viewModel::connectToDevice
            onDeviceClick = viewModel::connectToESP
        )
    }
    composable(ROUTE_SAVED_DEVICES){
        //HomeScreen(onClickGotoBluetoothScreen = { route -> appState.navigate (route) })
        //viewModel.updatePairedDevice()
        SavedDevices(
            pairedDevice = uiState.pairedDevices,
            onDeviceClick = viewModel::connectToESP
        )
    }
    composable(ROUTE_SETTINGS){
        Settings()
    }
    composable(ROUTE_INFO){
        Info()
    }
}