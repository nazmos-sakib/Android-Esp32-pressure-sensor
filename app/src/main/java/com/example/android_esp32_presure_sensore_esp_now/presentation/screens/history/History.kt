package com.example.android_esp32_presure_sensore_esp_now.presentation.screens.history

import android.graphics.drawable.Icon
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.android_esp32_presure_sensore_esp_now.presentation.BluetoothUiState


@Composable
fun History(
    state: BluetoothUiState,
    onDetailsSession: (sessionId: Long) -> Unit
) {

    val flag = remember {
        mutableStateOf<Boolean>(false)
    }



    when (flag.value) {
        true -> {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            flag.value = false
                        })
                }
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    items(state.sessionDetails) { item ->
                        item.headValue?.let {
                            item.bodyValue?.let { it1 ->
                                HistoryCardForSessionDetails(
                                    headValue = it,
                                    bodyValue = it1,
                                    timestamp = item.timestamp
                                )
                            }
                        }
                        //HistoryCardForSessionCategoryPreview()
                    }
                }
            }
        }

        false -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                items(state.sessionCategory) { item ->
                    HistoryCardForSessionCategory(
                        sessionId = item.sessionId,
                        practiceCount = item.count,
                        onItemClick = { sessionId ->
                            flag.value = true
                            onDetailsSession(sessionId)
                        }
                    )
                    //HistoryCardForSessionCategoryPreview()
                }
            }
        }
    }


}
