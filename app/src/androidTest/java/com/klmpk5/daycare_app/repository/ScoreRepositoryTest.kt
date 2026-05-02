package com.klmpk5.daycare_app.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.klmpk5.daycare_app.data.local.db.DaycareDatabase
import com.klmpk5.daycare_app.data.local.entities.DailyScore
import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_app.data.remote.model.DailyScoreRemoteDto
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

class ScoreRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: ScoreRepository
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
        repository = ScoreRepository(database.scoreDao(), firebaseService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAddScore() = runBlocking {
        withTimeout(10000) {
            val score = DailyScore(
                scoreId = "SCORE_TEST_001",
                childId = "CHILD_999",
                date = "02/05/2026",
                activityName = "Mewarnai",
                score = "A",
                notes = "Sangat rapi"
            )

            withContext(Dispatchers.IO) {
                repository.addScore(score)

                val localScores = repository.getScoresByChildLocal("CHILD_999").first()
                assertEquals(1, localScores.size)
                assertEquals("A", localScores[0].score)

                val remoteScores = firebaseService.getScoresByChild("CHILD_999")
                assertTrue(remoteScores.any { it.scoreId == "SCORE_TEST_001" })
            }
        }
    }

    @Test
    fun testSyncScores() = runBlocking {
        withTimeout(20000) {
            withContext(Dispatchers.IO) {
                val dummyScore = DailyScoreRemoteDto(
                    scoreId = "SCORE_SYNC_001",
                    childId = "CHILD_SYNC_999",
                    date = "03/05/2026",
                    activityName = "Membaca",
                    score = "B+",
                    notes = "Lancar"
                )
                firebaseService.addScore(dummyScore)

                kotlinx.coroutines.delay(2000)

                repository.syncScoresFromRemote("CHILD_SYNC_999")

                val syncedLocal = repository.getScoresByChildLocal("CHILD_SYNC_999").first()
                assertTrue(syncedLocal.isNotEmpty())
                assertEquals("Membaca", syncedLocal[0].activityName)
            }
        }
    }
}