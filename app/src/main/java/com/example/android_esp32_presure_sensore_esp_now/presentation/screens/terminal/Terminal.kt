package com.example.android_esp32_presure_sensore_esp_now.presentation.screens.terminal

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_esp32_presure_sensore_esp_now.R
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothMessage
import com.example.android_esp32_presure_sensore_esp_now.domain.TerminalScreenState
import com.example.android_esp32_presure_sensore_esp_now.presentation.BluetoothUiState
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun Terminal(
    state: BluetoothUiState,
    onDisconnect: () -> Unit = {},
    onSessionStart: () -> Unit = {},
    onSessionEnd: () -> Unit = {},
) {

    when (state.terminalUIState) {
        TerminalScreenState.Connected -> ConnectedScreen (
            onSessionStart = onSessionStart
        )
        //TerminalScreenState.Connecting -> OnASessionScreen()
        TerminalScreenState.Training -> OnASessionScreen(
            state = state,
            onEndSession=onSessionEnd
        )

        TerminalScreenState.ConnectedAndLoading -> {}
        TerminalScreenState.NotConnected ->  NotConnectedScreen()
        TerminalScreenState.Connecting -> NotConnectedScreen()
    }
}


@Preview
@Composable
fun OnASessionScreenPreview(){
    OnASessionScreen(BluetoothUiState())
}

@Composable
fun OnASessionScreen(
    state: BluetoothUiState,
    onEndSession:()->Unit={}
) {
    val sortedMessages = remember { mutableStateListOf<BluetoothMessage>() }

    val clampedHead = remember {
        mutableIntStateOf(0)
    }
    val clampedBody = remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(key1 = state.lastMessage) {
        val values  = state.lastMessage?.split(",") // Split the string on the comma

        if (values != null) {
            if (values.size == 2) { // Ensure there are two values
                clampedHead.intValue = values[0].toInt().coerceIn(0,255) // Parse the first value
                clampedBody.intValue = values[1].toInt().coerceIn(0, 255) // Parse the second value

            } else {
                println("Received unexpected data: ")
            }
        }
    }

    LaunchedEffect(key1 = state.messages) {
        Log.d("Terminal", "OnASessionScreen: .message changed")

        // Clear and update the contents of the mutableStateListOf
        sortedMessages.clear()
        sortedMessages.addAll(state.messages.sortedByDescending { it.timestamp })
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier,
                contentAlignment = Alignment.TopCenter,
            ) {
                Column {
                    Text(
                        text = "Head Score: ", color = Color.White,
                        fontSize = 20.sp,
                        fontStyle = null,
                        fontWeight = FontWeight.Bold,
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Body Score: ", color = Color.White,
                        fontSize = 20.sp,
                        fontStyle = null,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            Box(
                modifier = Modifier,
                contentAlignment = Alignment.TopCenter,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.head), contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp),
                    tint =  Color(red = clampedHead.intValue / 255f, green = (255 - clampedHead.intValue) / 255f, blue = (255 - clampedHead.intValue) / 255f)
                )

                Icon(
                    painter = painterResource(id = R.drawable.body),
                    contentDescription = null,
                    modifier = Modifier
                        .height(200.dp)
                        .width(200.dp),
                    tint = Color(red = clampedBody.intValue / 255f, green = (255 - clampedBody.intValue) / 255f, blue = (255 - clampedBody.intValue) / 255f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        //lists
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(vertical = 10.dp)
        ) {
            items(sortedMessages) {aMessage->
                val values  = aMessage.message.split(",") // Split the string on the comma
                val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                if (values.size == 2) {
                    CardItem(
                        values[0].toInt() ,
                        values[1].toInt(),
                        formatter.format(aMessage.timestamp)
                    )
                }
            }
        }

        Button(
            onClick = { onEndSession() },
            modifier = Modifier
                .padding(5.dp)
                .fillMaxWidth(.9f)
                .border(2.dp, color = Color.White, shape = RoundedCornerShape(10.dp)),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonColors(
                containerColor = Color.Transparent,
                contentColor = Color.White,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.White

            )

        ) {
            Text(text = "End Session", fontSize = 20.sp)
        }

    }
}

@Preview
@Composable
fun ConnectedScreen(
    onSessionStart: () -> Unit = {}
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(.7f)
                .clickable {
                    onSessionStart()
                }
                .border(2.dp, color = Color.White, shape = RoundedCornerShape(10.dp))
                .padding(20.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.target_1212), contentDescription = null,
                modifier = Modifier
                    .height(100.dp)
                    .width(100.dp),
                tint = Color.White
            )
            Text(
                text = "Start a Session",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

//@Preview
@Composable
fun NotConnectedScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.bluetooth_not_connected),
            contentDescription = null,
            modifier = Modifier
                .height(200.dp)
                .width(200.dp),
            tint = Color.Blue,
        )

        Text(text = "Not connected to any device", color = Color.White, fontSize = 28.sp)
    }
}