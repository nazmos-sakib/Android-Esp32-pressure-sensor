package com.example.android_esp32_presure_sensore_esp_now.data.db.repository

import com.example.android_esp32_presure_sensore_esp_now.data.db.Session
import com.example.android_esp32_presure_sensore_esp_now.data.db.SessionIdCount
import com.example.android_esp32_presure_sensore_esp_now.db.SessionDataBase
import com.example.android_esp32_presure_sensore_esp_now.db.repository.SessionRepository
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothMessage
import com.example.android_esp32_presure_sensore_esp_now.presentation.screens.terminal.CardItem
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor (
    private val db: SessionDataBase
) : SessionRepository {
    override fun getAllSessions() = db.getSessionDao().getAllSessions()
    override fun getSessionsById(sessionId: Long) = db.getSessionDao().getSessionsById(sessionId)

    override suspend fun insert(session: Session) = db.getSessionDao().insert(session)

    override suspend fun insertAll(sessionID: Long, messages: List<BluetoothMessage>) {
        messages.forEach { item ->
            val values  =  item.message.split(",") // Split the string on the comma
            if (values.size == 2) {
                insert(
                    Session(
                        sessionId = sessionID,
                        headValue = values[0] ,
                        bodyValue = values[1],
                        timestamp = item.timestamp
                    )
                )
            }
        }

    }

    override suspend fun deleteArticle(session: Session) = db.getSessionDao().deleteSession(session)

    override suspend fun getSessionIdOccurrences(): List<SessionIdCount> = db.getSessionDao().getSessionIdOccurrences()
    override suspend fun getSessionsBySessionId(sessionId: Long):List<Session> = db.getSessionDao().getSessionsBySessionId(sessionId)


}