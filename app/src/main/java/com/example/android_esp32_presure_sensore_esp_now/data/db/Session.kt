package com.example.android_esp32_presure_sensore_esp_now.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.Date


@Entity(tableName = "session")
data class Session(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var sessionId: Long,
    val headValue: String?,
    val bodyValue: String?,
    val timestamp: Date
):Serializable

{
    override fun hashCode(): Int {
        var result = id.hashCode()

        result = 31 * result + sessionId.hashCode()


        if(headValue.isNullOrEmpty()){
            result = 31 * result + headValue.hashCode()
        }
        if(bodyValue.isNullOrEmpty()){
            result = 31 * result + bodyValue.hashCode()
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Session

        if (id != other.id) return false
        if (sessionId != other.sessionId) return false
        if (headValue != other.headValue) return false
        if (bodyValue != other.bodyValue) return false

        return true
    }
}