package com.klmpk5.daycare_app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.data.local.entities.DailyScore
import com.klmpk5.daycare_app.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_app.data.local.dao.ChildDao
import com.klmpk5.daycare_app.data.local.dao.ScoreDao
import com.klmpk5.daycare_app.data.local.dao.WeeklyPlanDao

@Database(
    entities = [Child::class, WeeklyPlan::class, DailyScore::class],
    version = 1,
    exportSchema = false
)
abstract class DaycareDatabase : RoomDatabase() {
    abstract fun childDao(): ChildDao
    abstract fun weeklyPlanDao(): WeeklyPlanDao
    abstract fun scoreDao(): ScoreDao
}