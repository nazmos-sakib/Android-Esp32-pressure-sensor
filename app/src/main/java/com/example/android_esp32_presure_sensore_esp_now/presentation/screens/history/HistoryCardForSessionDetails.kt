package com.example.android_esp32_presure_sensore_esp_now.presentation.screens.history

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_esp32_presure_sensore_esp_now.R
import com.example.android_esp32_presure_sensore_esp_now.common.integerToFormatedColorText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun HistoryCardForSessionDetails(
    headValue: String,
    bodyValue: String,
    timestamp: Date
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val clampedHead = remember {
            mutableIntStateOf(0)
        }
        val clampedBody = remember {
            mutableIntStateOf(0)
        }

        clampedHead.intValue = headValue.toInt().coerceIn(0,255) // Parse the first value
        clampedBody.intValue = bodyValue.toInt().coerceIn(0, 255) // Parse the second value

        Box(
            modifier = Modifier.width(50.dp),
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
        Spacer(modifier = Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = integerToFormatedColorText( "Head Count: ", headValue.toInt()),color = Color.White, fontSize = 20.sp)
            Text(text = integerToFormatedColorText( "Body Count: ", bodyValue.toInt()), color = Color.White, fontSize = 20.sp)

        }
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        Text(text = formatter.format(timestamp), modifier = Modifier
            .padding(top = 5.dp)
            .align(alignment = Alignment.Top),color = Color.White)
    }
}

@Preview
@Composable
fun HistoryCardPreview(){
    HistoryCardForSessionDetails(
        0.toString(),
        2010.toString(),
        Date()
    )
}