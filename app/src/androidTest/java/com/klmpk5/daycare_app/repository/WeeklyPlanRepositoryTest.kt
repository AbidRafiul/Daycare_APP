package com.klmpk5.daycare_app.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.klmpk5.daycare_app.data.local.db.DaycareDatabase
import com.klmpk5.daycare_app.data.local.entities.WeeklyPlan
import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_app.data.remote.model.WeeklyPlanRemoteDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeeklyPlanRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: WeeklyPlanRepository
    private lateinit var firebaseService: FirebaseService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        try { FirebaseApp.initializeApp(context) } catch (e: Exception) {}

        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()

        database = Room.inMemoryDatabaseBuilder(context, DaycareDatabase::class.java).build()
        firebaseService = FirebaseService()
        repository = WeeklyPlanRepository(database.weeklyPlanDao(), firebaseService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAddWeeklyPlan() = runBlocking {
        withTimeout(10000) {
            val plan = WeeklyPlan(
                planId = "PLAN_TEST_001",
                startDate = "01/05/2026",
                endDate = "05/05/2026",
                description = "Minggu Kesenian",
                imageUrl = null
            )

            withContext(Dispatchers.IO) {
                repository.addWeeklyPlan(plan)

                val localPlans = repository.getAllWeeklyPlansLocal().first()
                assertEquals(1, localPlans.size)
                assertEquals("Minggu Kesenian", localPlans[0].description)

                val remotePlans = firebaseService.getAllWeeklyPlans()
                assertTrue(remotePlans.any { it.planId == "PLAN_TEST_001" })
            }
        }
    }

    @Test
    fun testSyncWeeklyPlans() = runBlocking {
        withTimeout(20000) {
            withContext(Dispatchers.IO) {
                val dummyPlan = WeeklyPlanRemoteDto(
                    planId = "PLAN_SYNC_001",
                    startDate = "10/05/2026",
                    endDate = "14/05/2026",
                    description = "Minggu Olahraga",
                    imageUrl = null
                )
                firebaseService.addWeeklyPlan(dummyPlan)

                kotlinx.coroutines.delay(2000)

                repository.syncWeeklyPlansFromRemote()

                val syncedLocal = repository.getAllWeeklyPlansLocal().first()
                assertTrue(syncedLocal.isNotEmpty())
                assertTrue(syncedLocal.any { it.description == "Minggu Olahraga" })
            }
        }
    }
}