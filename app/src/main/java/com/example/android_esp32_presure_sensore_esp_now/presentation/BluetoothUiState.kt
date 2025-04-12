package com.example.android_esp32_presure_sensore_esp_now.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android_esp32_presure_sensore_esp_now.data.db.Session
import com.example.android_esp32_presure_sensore_esp_now.data.db.SessionIdCount
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothDeviceDomain
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothMessage
import com.example.android_esp32_presure_sensore_esp_now.domain.TerminalScreenState

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val pairedDevices: List<BluetoothDeviceDomain> = emptyList(),
    val isConnected:Boolean = false,
    val isConnecting:Boolean = false,
    val errMessage:String? = null,
    val messages:List<BluetoothMessage> = emptyList(),
    val sessionCategory:List<SessionIdCount> = emptyList(),
    val sessionDetails:  List<Session> = emptyList() ,
    val lastMessage:String? = null,
    var terminalUIState:TerminalScreenState  = TerminalScreenState.NotConnected,
)
