package com.example.android_esp32_presure_sensore_esp_now.presentation.screens.history

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.android_esp32_presure_sensore_esp_now.common.getCombinedDateTimeAsString


@Composable
fun HistoryCardForSessionCategory(
    sessionId:Long,
    practiceCount:Int,
    onItemClick:(Long)->Unit = {},
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable {
                onItemClick(sessionId)
            },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text = getCombinedDateTimeAsString(sessionId), color = Color.White)
        Text(text = "Total Count: $practiceCount",  color = Color.White)

    }
}

@Preview
@Composable
fun HistoryCardForSessionCategoryPreview(){
    HistoryCardForSessionCategory(
        sessionId = 20250108113135,
        practiceCount = 45,
        onItemClick = {}
    )
}
