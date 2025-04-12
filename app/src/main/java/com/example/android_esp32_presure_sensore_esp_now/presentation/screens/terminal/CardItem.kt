package com.example.android_esp32_presure_sensore_esp_now.presentation.screens.terminal

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_esp32_presure_sensore_esp_now.presentation.ui.theme.AndroidEsp32presuresensoreESPNOWTheme

@Composable
fun CardItem(
    headValue: Int,
    bodyValue: Int,
    time:String
){

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(width = 2.dp, color = Color.DarkGray, shape = RoundedCornerShape(5.dp))
            .padding(8.dp)
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text(text = integerToFormatedText("Head Score: ",headValue),color  = Color.White,
                fontSize  = 20.sp,
                fontStyle  = null,
                fontWeight  = FontWeight.Bold,)

            Text(text = time,color  = Color.White,
                fontSize  = 10.sp,
                fontStyle  = null,
                fontWeight  = FontWeight.Bold,
                )
        }



        Spacer(modifier = Modifier.height(15.dp))

        Text(text =  integerToFormatedText("Body Score: ",bodyValue),color  = Color.White,
            fontSize  = 20.sp,
            fontStyle  = null,
            fontWeight  = FontWeight.Bold,)
    }
}

/*
  if (headReading > 100 && headReading < 1500)  // from 100 to 1499
    Serial.println("headReading -> light touch");
  else if (headReading >= 1500 && headReading < 2500)  // from 1500 to 2499
    Serial.println("headReading -> light squeeze");
  else if (headReading >= 2500 && headReading < 3500)  // from 2500 to 3499
    Serial.println("headReading -> medium squeeze");
  else if (headReading >= 3500)  // from 3500 to 5000
    Serial.println("headReading -> big squeeze");
 */

fun integerToFormatedText(str:String,int:Int): AnnotatedString {
    val color = when {
        int in 100..1499 -> Color.LightGray
        int in 1500..2499 ->  Color.Gray
        int in 2500..3499 ->  Color.DarkGray
        int >= 3500 ->  Color.Blue
        else -> Color.White
    }

    return buildAnnotatedString {
        append(str)
        withStyle(style = SpanStyle(color = color, fontWeight = FontWeight.Bold)) {
            append(int.toString())
        }
    }
}

@Preview
@Composable
fun CardItemPreview(){
    AndroidEsp32presuresensoreESPNOWTheme {
        CardItem(200,2300,"HH:MM:SS")
    }
}