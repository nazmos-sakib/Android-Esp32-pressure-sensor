package com.example.android_esp32_presure_sensore_esp_now.data

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.util.Log
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothControllerForESP
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothDeviceDomain
import com.example.android_esp32_presure_sensore_esp_now.domain.ConnectionResult
import com.example.android_esp32_presure_sensore_esp_now.domain.ERROR_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.UUID

@SuppressLint("MissingPermission")
class AndroidBluetoothControllerForESP(
    private val context: Context
) : BluetoothControllerForESP {
    private val TAG:String = "BluetoothControllerForESP->"

    private val bluetoothManager by lazy {
        context.getSystemService(BluetoothManager::class.java)
    }
    private val bluetoothAdapter by lazy {
        bluetoothManager?.adapter
    }


    private var dataTransferService:BluetoothDataTransferService? = null

    private val _isConnected = MutableStateFlow<Boolean>(false)
    override val isConnected: StateFlow<Boolean>
        get() = _isConnected.asStateFlow()

    private val _scannedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _scannedDevices.asStateFlow()


    private val _pairedDevices = MutableStateFlow<List<BluetoothDeviceDomain>>(emptyList())
    override val pairedDevices: StateFlow<List<BluetoothDeviceDomain>>
        get() = _pairedDevices.asStateFlow()


    private val _errors = MutableSharedFlow<String>()
    override val errors: SharedFlow<String>
        get() = _errors.asSharedFlow()

    //broadcast receiver -----------------------------------------------
    //broadcast receiver to get bluetooth device list when search
    private val foundDeviceReceiver = FoundDeviceReceiver{ device ->
        _scannedDevices.update { devices ->
            val newDevice = device.toBluetoothDeviceDomain()
            if (newDevice in devices) devices else devices + newDevice
        }
    }

    //broadcast receiver - when a connection is interrupted with an existed connected device
    private val bluetoothStateReceiver = BluetoothStateReceiver{ isConnected, bluetoothDevice ->
        if (bluetoothAdapter?.bondedDevices?.contains(bluetoothDevice) == true){
            _isConnected.update {isConnected}
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                _errors.emit("Disconnected: can't connect to a non-paired device")
                _isConnected.update {isConnected}
            }
        }
    }

    //broadcast receiver for observing pairing mode
    private val pairingReceiver = PairingReceiver { device ->
        //connectToDevice(device)  // Automatically connect after pairing
    }

    //on connection to a device observe the bonding with the device
    private val bondStateReceiver = BondStateReceiver{
        updatePairedDevises()
        //context.unregisterReceiver(bondStateReceiver)
    }
    //end broadcast receiver -----------------------------------------------


    private var activeSocket: BluetoothSocket? = null

    init {
        updatePairedDevises()
        //register bluetoothStateReceiver (disconnected) receiver
        context.registerReceiver(
            bluetoothStateReceiver,
            //intent_filter take one arguments
            IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(android.bluetooth.BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
        )

        //register pairing receiver
        context.registerReceiver(
            pairingReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        )
    }

    override fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }
        //register a broadcast receiver to update
        context.registerReceiver(
            foundDeviceReceiver,
            IntentFilter(android.bluetooth.BluetoothDevice.ACTION_FOUND)
        )

        updatePairedDevises()
        bluetoothAdapter?.startDiscovery()
    }

    override fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)){
            return
        }
        bluetoothAdapter?.cancelDiscovery()
    }

    override fun updatePairedDevises(){
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
            return
        }
        bluetoothAdapter
            ?.bondedDevices
            ?.map{
                it.toBluetoothDeviceDomain()
            }
            ?.also { devices->
                println("device size: ${devices.size}")
                _pairedDevices.update { devices }
            }
    }



    override fun connectToESP(device: BluetoothDeviceDomain,callback: ()->Unit){

        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)){
            throw SecurityException("No BLUETOOTH_CONNECT permission")
        }
        val clintDevice: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(device.address)
        val uuid: UUID? = clintDevice?.uuids?.get(0)?.uuid // UUID provided by your ESP32 service
        val clintSocket: BluetoothSocket? = clintDevice?.createRfcommSocketToServiceRecord(uuid)

        // Ensure Bluetooth discovery is stopped before trying to connect
        bluetoothAdapter?.cancelDiscovery()

        clintSocket?.let { socket->
            try {
                socket.connect()
                _isConnected.value = true
                callback() //change status
                activeSocket = socket
            } catch (e:IOException){
                Log.d(ERROR_TAG, "connectToDevice: ${e.message}")
                socket.close()
                _isConnected.value = false
                activeSocket = null
            }
        }
    }


    override fun startAWorkoutSession(): Flow<ConnectionResult> {
        return flow {
            activeSocket?.let { socket->
                try {
                    //socket.connect()
                    BluetoothDataTransferService(socket).also {
                        dataTransferService = it
                        emitAll(
                            it.listenForIncomingMessages()
                                .map {bluetoothMessage->
                                    ConnectionResult.TransferSucceeded(bluetoothMessage)
                                }
                        )
                    }
                } catch (e: IOException){
                    Log.d(ERROR_TAG, "connectToDevice: ${e.message}")
                    socket.close()
                    activeSocket = null
                    emit(ConnectionResult.Error("Connection was interrupted: ${e.message}"))
                }
            }
        }.onCompletion {
            //closeConnection()
        }.flowOn(Dispatchers.IO)

    }

    override fun closeAllConnection()  {
        activeSocket?.close()
        activeSocket = null
    }

    override fun closeConnection() {
        activeSocket?.close()
        activeSocket = null
    }

    override fun releaseAll() {
        releaseBroadCastReceivers()
        closeAllConnection()
    }

    override fun releaseBroadCastReceivers() {
        context.unregisterReceiver(foundDeviceReceiver)
        context.unregisterReceiver(bluetoothStateReceiver)
        context.unregisterReceiver(pairingReceiver)    }

    private fun hasPermission(permission:String):Boolean{
        return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val SERVICE_UUID = "6fb9c1f2-243e-4e54-8ea3-6d650c2f2c8f"
    }

}