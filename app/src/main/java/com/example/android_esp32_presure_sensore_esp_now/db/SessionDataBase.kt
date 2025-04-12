package com.example.android_esp32_presure_sensore_esp_now.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.android_esp32_presure_sensore_esp_now.common.DB_NAME
import com.example.android_esp32_presure_sensore_esp_now.data.db.Session

@Database(
    entities = [Session::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class SessionDataBase:RoomDatabase(){
    abstract fun getSessionDao():SessionDAO
    companion object{
        @Volatile  //other thread can see when one thread change the instances
        private var instance: SessionDataBase? = null
        private val  LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance ?:createDatabase(context).also{ instance=it}
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context,
            SessionDataBase::class.java,
            DB_NAME,
        ).build()
    }
}