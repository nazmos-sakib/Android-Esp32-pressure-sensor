package com.example.android_esp32_presure_sensore_esp_now.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_esp32_presure_sensore_esp_now.common.getCombinedDateTimeAsLong
import com.example.android_esp32_presure_sensore_esp_now.data.db.repository.SessionRepositoryImpl
 import com.example.android_esp32_presure_sensore_esp_now.db.repository.SessionRepository
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothController
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothControllerForESP
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothDeviceDomain
import com.example.android_esp32_presure_sensore_esp_now.domain.ConnectionResult
import com.example.android_esp32_presure_sensore_esp_now.domain.TerminalScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class BluetoothViewModel @Inject constructor(
    private val bluetoothController: BluetoothControllerForESP,
    private val sessionRep:  SessionRepository
) : ViewModel() {
    private val TAG:String = "BluetoothViewModel->"
    private val _state = MutableStateFlow(BluetoothUiState())
    //    val state: State<CoinListState> = _state
    val state = combine(
        bluetoothController.scannedDevices,
        bluetoothController.pairedDevices,
        _state
    ){ scannedDevices, pairedDevices, state ->
        state.copy(
            scannedDevices = scannedDevices,
            pairedDevices = pairedDevices,
            messages = if(state.isConnected)  state.messages else emptyList()
        )
    }.stateIn(viewModelScope,SharingStarted.WhileSubscribed(5000),_state.value)


    private var deviceConnectionJob : Job?  = null
    private var workoutSessionJob : Job?  = null


    private lateinit var currentConnectedBluetoothDevice: BluetoothDeviceDomain


    init {
        //init observers
        bluetoothController.isConnected.onEach { isConnected->
            _state.update { state->
                state.copy(
                    isConnected = isConnected,
                    terminalUIState = if(isConnected) TerminalScreenState.Connected else TerminalScreenState.NotConnected
                ) }
        }.launchIn(viewModelScope)

        bluetoothController.errors.onEach { error->
            _state.update {  it.copy(  errMessage = error ) }
        }.launchIn(viewModelScope)

    }



    /*
    fun connectToESPExp(device:BluetoothDeviceDomain){
        Log.d(TAG, "connectToESPExp: ")
        _state.update { it.copy(isConnecting = true) }
        //waitForIncomingConnection() //newly added
        deviceConnectionJob = bluetoothController.connectToESPExp(device).listen() //call the extension function
    }
    */
    fun connectToESP(device:BluetoothDeviceDomain,onDeviceConnect: ()->Unit){
        _state.update { it.copy(isConnecting = true) }
         bluetoothController.connectToESP(device){onDeviceConnect()}//call the extension function
    }
    fun startAWorkoutSession(){
        Log.d(TAG, "startAWorkoutSession: ")
        _state.update { it.copy(terminalUIState = TerminalScreenState.Training) }
        //waitForIncomingConnection() //newly added
        workoutSessionJob = bluetoothController.startAWorkoutSession().listen() //call the extension function
    }

    fun disconnectFromDevice(){
        Log.d(TAG, "disconnectFromDevice: ")
        workoutSessionJob?.cancel()
        bluetoothController.closeConnection()
        _state.update {  it.copy(  isConnecting = false, isConnected = false, terminalUIState = TerminalScreenState.NotConnected ) }
    }

    fun endWorkoutSession(){
        Log.d(TAG, "endWorkoutSession: triggered")
//        _state.update {  it.copy(  terminalUIState = TerminalScreenState.ConnectedAndLoading ) }

        workoutSessionJob?.cancel()

        viewModelScope.launch {
            try {
                sessionRep.insertAll(
                    getCombinedDateTimeAsLong(Date()),
                    state.value.messages
                )
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }

        _state.update {  it.copy(
            terminalUIState = TerminalScreenState.Connected,
            messages = emptyList() ,
            lastMessage = "0,0"
        ) }
    }


    fun startScan(){
        bluetoothController.startDiscovery()
    }

    fun stopScan(){
        bluetoothController.stopDiscovery()
    }

    fun updatePairedDevice(){
      bluetoothController.updatePairedDevises()
    }

    private fun Flow<ConnectionResult>.listen():Job{
        return onEach { result->
            when(result){
                is ConnectionResult.ConnectionEstablished ->{
                    Log.d("Terminal", "Current terminalUIState: ${_state.value.terminalUIState}")

                    _state.update {currentState->
                        currentState.copy(
                            isConnected = true,
                            isConnecting = false,
                            errMessage = null,
                            terminalUIState = if(currentState.terminalUIState==TerminalScreenState.NotConnected) TerminalScreenState.Connected else currentState.terminalUIState
                        )
                    }
                }

                is ConnectionResult.TransferSucceeded ->{
                    _state.update {
                        Log.d("esp-message--->", "listen: $result.message")
                        it.copy(
                            lastMessage = result.message.message,
                            messages = it.messages + result.message,
                            terminalUIState = TerminalScreenState.Training
                    ) }
                }

                is ConnectionResult.Error -> {
                    _state.update {
                        it.copy(
                            isConnected = false,
                            isConnecting = false,
                            errMessage = result.message,
                            terminalUIState = TerminalScreenState.NotConnected
                        )
                    }
                }
            }
        }.catch { throwable->
            bluetoothController.closeConnection()
            _state.update {
                it.copy(
                    isConnected = false,
                    isConnecting = false,
                    terminalUIState = TerminalScreenState.NotConnected
                )
            }

        }.launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothController.releaseAll()
    }

    fun updateSessionIdOccurrences(){
        _state.update {
            it.copy(sessionCategory = emptyList())
        }
        viewModelScope.launch((Dispatchers.IO)) {
            try {
                _state.update {
                    it.copy(sessionCategory = sessionRep.getSessionIdOccurrences())
                }
            } catch (e: Exception) {
                println("Error-<: ${e.message}")
            }
        }
    }

    fun updateSessionDetailsBySessionId(sessionId:Long){
        _state.update {
            it.copy(sessionDetails =  emptyList())
        }
        viewModelScope.launch((Dispatchers.IO)) {
            try {

                _state.update {
                    it.copy(sessionDetails =  sessionRep.getSessionsBySessionId(sessionId))
                }
            } catch (e: Exception) {
                println("Error-<: ${e.message}")
            }
        }
    }


}