package com.example.android_esp32_presure_sensore_esp_now.common

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle


fun integerToFormatedColorText(str:String,int:Int): AnnotatedString {
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