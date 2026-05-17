package com.klmpk5.daycare_app

import android.app.Application
import androidx.room.Room
import com.klmpk5.daycare_app.data.local.db.DaycareDatabase
import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_app.repository.ChildRepository
import com.klmpk5.daycare_app.repository.ScoreRepository
import com.klmpk5.daycare_app.repository.WeeklyPlanRepository
import com.klmpk5.daycare_app.repository.UserRepository

class App : Application() {

    // 1. Deklarasi Database & Service
    lateinit var database: DaycareDatabase
        private set

    lateinit var firebaseService: FirebaseService
        private set

    // 2. Deklarasi Repositories
    lateinit var childRepository: ChildRepository
        private set
    lateinit var weeklyPlanRepository: WeeklyPlanRepository
        private set
    lateinit var scoreRepository: ScoreRepository
        private set
    lateinit var userRepository: UserRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi Room Database (hanya dibuat 1x saat aplikasi jalan)
        database = Room.databaseBuilder(
            applicationContext,
            DaycareDatabase::class.java,
            "daycare_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        // Inisialisasi Firebase Service
        firebaseService = FirebaseService()

        // Inisialisasi semua Repositories dengan memasukkan DAO dan FirebaseService
        childRepository = ChildRepository(database.childDao(), firebaseService)
        weeklyPlanRepository = WeeklyPlanRepository(database.weeklyPlanDao(), firebaseService)
        scoreRepository = ScoreRepository(database.scoreDao(), firebaseService)
        userRepository = UserRepository(database.userDao(), firebaseService)
    }
}
