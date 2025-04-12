package com.example.android_esp32_presure_sensore_esp_now.domain

sealed interface ConnectionResult {
    data object ConnectionEstablished : ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage): ConnectionResult
    data class Error(val message:String): ConnectionResult
}