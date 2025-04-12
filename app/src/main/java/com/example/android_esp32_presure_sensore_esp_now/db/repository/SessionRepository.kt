package com.example.android_esp32_presure_sensore_esp_now.db.repository

import androidx.lifecycle.LiveData
import com.example.android_esp32_presure_sensore_esp_now.data.db.Session
import com.example.android_esp32_presure_sensore_esp_now.data.db.SessionIdCount
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothMessage

interface  SessionRepository {

        fun getAllSessions(): LiveData<List<Session>>
        fun getSessionsById(sessionId: Long): LiveData<List<Session>>

        suspend fun insertAll(sessionID:Long,messages:List<BluetoothMessage>)

        suspend fun insert(session: Session):Long

        suspend fun deleteArticle(session: Session)

        suspend fun getSessionIdOccurrences(): List<SessionIdCount>
        suspend fun getSessionsBySessionId(sessionId: Long): List<Session>
}