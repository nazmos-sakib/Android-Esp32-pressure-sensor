package com.example.android_esp32_presure_sensore_esp_now.domain


typealias BluetoothDeviceDomain = BluetoothDevice

data class BluetoothDevice(
    val name:String?,
    val address:String,
    val type: Int,
    val bondState:Int
)