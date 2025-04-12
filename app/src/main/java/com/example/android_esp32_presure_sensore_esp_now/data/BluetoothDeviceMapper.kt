package com.example.android_esp32_presure_sensore_esp_now.data

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain():BluetoothDeviceDomain{
    return BluetoothDeviceDomain(
        name = name,
        address = address,
        type = type,
        bondState = bondState
    )
}