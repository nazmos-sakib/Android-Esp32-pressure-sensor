package com.example.android_esp32_presure_sensore_esp_now.domain

sealed interface TerminalScreenState {
    data object Connected : TerminalScreenState
    data object ConnectedAndLoading : TerminalScreenState
    data object Connecting : TerminalScreenState
    data object NotConnected : TerminalScreenState
    data object Training : TerminalScreenState
}