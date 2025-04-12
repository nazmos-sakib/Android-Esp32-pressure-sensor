package com.example.android_esp32_presure_sensore_esp_now.presentation.screens.pair_new_devices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothDeviceDomain
import kotlin.reflect.KFunction2

@Composable
fun PairNewDevice(
    scannedDevices: List<BluetoothDeviceDomain>,
    onDeviceClick: KFunction2<BluetoothDeviceDomain, () -> Unit, Unit>,
    onDeviceConnect: ()->Unit,
    modifier: Modifier = Modifier
){
    LazyColumn(modifier = modifier) {
        item {
            Text(text = "Near by Devices", fontWeight = FontWeight.Bold, fontSize = 24.sp, modifier = Modifier.padding(16.dp))
        }
        items(scannedDevices){device->
           /* if(device.name.toString() != "null"){
                Text(
                    text = device.name.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onDeviceClick(device) }
                        .padding(16.dp)
                )
            }*/

            Text(
                text = device.name.toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDeviceClick(device) { onDeviceConnect() } }
                    .padding(16.dp)
            )
        }

    }
}