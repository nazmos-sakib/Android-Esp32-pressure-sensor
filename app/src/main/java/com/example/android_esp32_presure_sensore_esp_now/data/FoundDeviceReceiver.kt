package com.example.android_esp32_presure_sensore_esp_now.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class FoundDeviceReceiver(
    private val onDeviceFound:(BluetoothDevice) -> Unit
):BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            BluetoothDevice.ACTION_FOUND -> {
                val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(
                        BluetoothDevice.EXTRA_DEVICE,
                        BluetoothDevice::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    //intent.getParcelableExtra(BluetoothDevice.EXTRA_NAME)
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                }
                device?.let {d->
                    onDeviceFound(d)
                }
            }
        }
    }

}