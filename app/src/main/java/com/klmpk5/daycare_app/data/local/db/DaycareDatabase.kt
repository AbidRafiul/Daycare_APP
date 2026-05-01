package com.namaapp.daycare.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.namaapp.daycare.data.local.dao.ChildDao
import com.namaapp.daycare.data.local.entities.Child

@Database(entities = [Child::class], version = 1, exportSchema = true)
abstract class DaycareDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
}