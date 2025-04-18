package com.example.android_esp32_presure_sensore_esp_now.data

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class BluetoothStateReceiver(
    private val onStateChange:(isConnected:Boolean,BluetoothDevice) -> Unit
):BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(
                BluetoothDevice.EXTRA_DEVICE,
                BluetoothDevice::class.java
            )
        } else {
            intent?.getParcelableExtra(BluetoothDevice.EXTRA_NAME)
        }
        when(intent?.action){
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                onStateChange(true,device?:return)
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                onStateChange(false,device?:return)
            }
        }
    }

}