package com.klmpk5.daycare_app

import android.app.Application
import androidx.room.Room
import com.namaapp.daycare.data.local.db.DaycareDatabase

class App : Application() {

    val database: DaycareDatabase by lazy {
        Room.databaseBuilder(applicationContext, DaycareDatabase::class.java, "daycare_db").build()
    }
}