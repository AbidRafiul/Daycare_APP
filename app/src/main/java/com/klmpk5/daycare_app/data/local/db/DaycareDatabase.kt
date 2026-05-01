package com.klmpk5.daycare_app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klmpk5.daycare_app.data.local.dao.ChildDao
import com.klmpk5.daycare_app.data.local.entities.Child

@Database(entities = [Child::class], version = 1, exportSchema = true)
abstract class DaycareDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
}