package com.example.android_esp32_presure_sensore_esp_now.data

import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothMessage
import java.util.Date


fun String.toBluetoothMessage(isFromLocalUser:Boolean,senderName:String): BluetoothMessage {
//    val name = substringBeforeLast("#")
//    val message = substringAfter("#")
    return BluetoothMessage(
        message = this,
        senderName = senderName,
        isFromLocalUser = isFromLocalUser,
        timestamp =  Date()
    )
}

fun BluetoothMessage.toByteArray():ByteArray{
    return "$senderName#$message\n".encodeToByteArray()
}