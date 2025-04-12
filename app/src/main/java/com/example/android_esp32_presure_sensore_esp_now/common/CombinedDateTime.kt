package com.example.android_esp32_presure_sensore_esp_now.common

import java.util.Calendar
import java.util.Date

fun getCombinedDateTimeAsLong(date: Date): Long {
    val calendar = Calendar.getInstance()
    calendar.time = date

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH) + 1 // Months are 0-indexed
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val hour = calendar.get(Calendar.HOUR_OF_DAY) // 24-hour format
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)

    // Combine into a single integer as YYYYMMDDHHMMSS
    return "$year%02d%02d%02d%02d%02d".format(month, day, hour, minute,second).toLong()
}


fun getCombinedDateTimeAsString(datetime: Long):  String  {
    val year = (datetime / 10000000000L).toInt() // Extract the year
    val month = ((datetime / 100000000) % 100).toInt() // Extract the month
    val day = ((datetime / 1000000) % 100).toInt() // Extract the day
    val hour = ((datetime / 10000) % 100).toInt() // Extract the hour
    val minute = ((datetime / 100) % 100).toInt() // Extract the minute
    val second = (datetime % 100).toInt() // Extract the second

    return "$year-%02d-%02d_%02d:%02d:%02d".format(month, day, hour, minute,second)
}