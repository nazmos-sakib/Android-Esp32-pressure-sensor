package com.example.android_esp32_presure_sensore_esp_now.domain

import java.io.IOException

class TransferFailedException:IOException("Reading Incoming Data failed")