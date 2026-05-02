package com.klmpk5.daycare_app.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.klmpk5.daycare_app.data.local.db.DaycareDatabase
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.data.remote.datasource.ChildRemoteDataSource
import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChildRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: ChildRepository
    private lateinit var firebaseService: FirebaseService
    private lateinit var remoteDataSource: ChildRemoteDataSource

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Inisialisasi Firebase secara eksplisit
        FirebaseApp.initializeApp(context)

        // --- TAMBAHAN KUNCI ---
        // Matikan Offline Persistence Firestore khusus untuk Testing.
        // Tujuannya: Kalau Firebase gagal konek/ditolak server, dia akan langsung CRASH, tidak macet.
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        // ----------------------

        database = Room.inMemoryDatabaseBuilder(
            context,
            DaycareDatabase::class.java
        ).build()

        firebaseService = FirebaseService()
        remoteDataSource = ChildRemoteDataSource(firebaseService)
        repository = ChildRepository(database.childDao(), remoteDataSource)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertChildToRoomAndFirebase() = runBlocking {
        // Paksa tes berhenti jika lebih dari 10 detik!
        kotlinx.coroutines.withTimeout(10000) {
            val child = Child(fullName = "John Doe", birthDate = "01/01/2015", gender = "Male", parentUserId = "parent123")

            withContext(Dispatchers.IO) {
                repository.insertChild(child)

                val children = repository.getAllChildren().first()

                assertEquals(1, children.size)
                assertEquals("John Doe", children[0].fullName)

                val firebaseChildren = firebaseService.getChildrenByParent("parent123")
                assertTrue(firebaseChildren.isNotEmpty())
            }
        }
    }

    @Test
    fun testSyncChildrenFromRoomToFirebase() = runBlocking {
        // Paksa tes berhenti jika lebih dari 10 detik!
        kotlinx.coroutines.withTimeout(10000) {
            val child = Child(fullName = "Jane Doe", birthDate = "02/02/2016", gender = "Female", parentUserId = "parent456")

            withContext(Dispatchers.IO) {
                repository.insertChild(child)
                repository.syncChildrenToFirebase(listOf(child))

                val firebaseChildren = firebaseService.getChildrenByParent("parent456")
                assertTrue(firebaseChildren.isNotEmpty())
            }
        }
    }
}