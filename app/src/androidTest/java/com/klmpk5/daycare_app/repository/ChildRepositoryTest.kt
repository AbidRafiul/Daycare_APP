package com.klmpk5.daycare_app.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.klmpk5.daycare_app.data.local.db.DaycareDatabase
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
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

class ChildRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: ChildRepository
    private lateinit var firebaseService: FirebaseService

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Inisialisasi Firebase secara eksplisit
        try {
            FirebaseApp.initializeApp(context)
        } catch (e: Exception) {
            // Abaikan jika sudah terinisialisasi
        }

        // 2. MATIKAN OFFLINE PERSISTENCE (Penting agar tes tidak hang berjam-jam!)
        val firestore = FirebaseFirestore.getInstance()
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()

        // 3. Buat Database In-Memory (Data akan hilang setelah tes selesai)
        database = Room.inMemoryDatabaseBuilder(
            context,
            DaycareDatabase::class.java
        ).build()

        // 4. Inisialisasi Service dan Repository (Tanpa DataSource)
        firebaseService = FirebaseService()
        repository = ChildRepository(database.childDao(), firebaseService)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testAddChildToRepository() = runBlocking {
        // Paksa timeout 10 detik agar tidak nge-hang jika internet/firebase bermasalah
        withTimeout(10000) {
            // Buat data anak tiruan (Sesuai dengan Entity baru kita)
            val child = Child(
                childId = "TEST_CHILD_001",
                fullName = "Budi Santoso",
                birthDate = "10/10/2020",
                gender = "Laki-laki",
                parentUserId = "PARENT_999",
                photoUrl = null,
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            withContext(Dispatchers.IO) {
                // 1. Panggil fungsi repository untuk menyimpan ke Room & Firebase
                repository.addChild(child)

                // 2. Cek apakah data tersimpan di Room (Lokal)
                val localChildren = repository.getAllChildrenLocal().first()
                assertEquals(1, localChildren.size)
                assertEquals("Budi Santoso", localChildren[0].fullName)

                // 3. Cek apakah data tersimpan di Firebase (Remote)
                val firebaseChildren = firebaseService.getChildrenByParent("PARENT_999")
                assertTrue(firebaseChildren.isNotEmpty())
                assertEquals("TEST_CHILD_001", firebaseChildren[0].childId)
            }
        }
    }
    @Test
    fun testSyncChildrenFromRemote() = runBlocking {
        withTimeout(20000) { // Perpanjang timeout sedikit untuk toleransi delay
            withContext(Dispatchers.IO) {
                // 1. Pastikan Room kosong terlebih dahulu
                val initialLocal = repository.getAllChildrenLocal().first()
                assertEquals(0, initialLocal.size)

                // 2. SIAPKAN DATA DI FIREBASE DULU (Agar tes ini mandiri)
                val dummyChild = com.klmpk5.daycare_app.data.remote.model.ChildRemoteDto(
                    childId = "SYNC_TEST_001",
                    fullName = "Anak Sinkronisasi",
                    birthDate = "11/11/2021",
                    gender = "Perempuan",
                    parentUserId = "PARENT_SYNC", // ID khusus untuk tes ini
                    photoUrl = null,
                    isActive = true,
                    createdAt = System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )
                firebaseService.addChild(dummyChild) // Lempar ke Firebase

                // Beri waktu Firebase 2 detik untuk indexing data sebelum ditarik
                kotlinx.coroutines.delay(2000)

                // 3. Panggil fungsi sinkronisasi (Tarik dari Firebase "PARENT_SYNC", simpan ke Room)
                repository.syncChildrenFromRemote("PARENT_SYNC")

                // 4. Cek apakah Room sekarang sudah berisi data yang ditarik dari Firebase
                val syncedLocal = repository.getAllChildrenLocal().first()
                assertTrue(syncedLocal.isNotEmpty()) // Tidak akan merah lagi
                assertEquals("PARENT_SYNC", syncedLocal[0].parentUserId)
                assertEquals("Anak Sinkronisasi", syncedLocal[0].fullName)
            }
        }
    }
}