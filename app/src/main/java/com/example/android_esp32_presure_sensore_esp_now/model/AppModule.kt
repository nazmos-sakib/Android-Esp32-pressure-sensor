package com.example.android_esp32_presure_sensore_esp_now.model

import android.content.Context
import androidx.room.Room
import com.example.android_esp32_presure_sensore_esp_now.common.DB_NAME
import com.example.android_esp32_presure_sensore_esp_now.data.AndroidBluetoothController
import com.example.android_esp32_presure_sensore_esp_now.data.AndroidBluetoothControllerForESP
import com.example.android_esp32_presure_sensore_esp_now.data.db.repository.SessionRepositoryImpl
 import com.example.android_esp32_presure_sensore_esp_now.db.SessionDataBase
import com.example.android_esp32_presure_sensore_esp_now.db.repository.SessionRepository
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothController
import com.example.android_esp32_presure_sensore_esp_now.domain.BluetoothControllerForESP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBluetoothController(@ApplicationContext context: Context): BluetoothController {
        return AndroidBluetoothController(context)
    }

    @Provides
    @Singleton
    fun provideBluetoothControllerForESP(@ApplicationContext context: Context): BluetoothControllerForESP {
        return AndroidBluetoothControllerForESP(context)
    }

    @Provides
    @Singleton
    fun provideSessionDatabase(@ApplicationContext context: Context):  SessionDataBase {
        return  Room.databaseBuilder(
            context,
            SessionDataBase::class.java,
            DB_NAME,
        ).build()
    }

    @Provides
    @Singleton
    fun providesSessionRepository(db:SessionDataBase): SessionRepository {
        return SessionRepositoryImpl(db)
    }
}