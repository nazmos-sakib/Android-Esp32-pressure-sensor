package com.example.android_esp32_presure_sensore_esp_now.domain

import java.util.Date


data class BluetoothMessage(
    val message:String,
    val senderName:String,
    val isFromLocalUser:Boolean,
    val timestamp: Date,
)
