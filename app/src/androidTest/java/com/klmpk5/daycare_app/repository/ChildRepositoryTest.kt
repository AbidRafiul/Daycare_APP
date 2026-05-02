package com.klmpk5.daycare_app.repository

import androidx.room.Room
import com.klmpk5.daycare_app.data.local.db.DaycareDatabase
import com.klmpk5.daycare_app.data.local.entities.Child
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import androidx.test.core.app.ApplicationProvider

class ChildRepositoryTest {

    private lateinit var database: DaycareDatabase
    private lateinit var repository: ChildRepository

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            DaycareDatabase::class.java
        ).build()
        repository = ChildRepository(database.childDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testInsertChild() = runBlocking {   // ← tambah runBlocking
        val child = Child(
            fullName = "John Doe",
            birthDate = "01/01/2015",
            gender = "Male",
            parentUserId = "parent123"
        )

        repository.insertChild(child)

        // getOrAwaitValue() diganti .first()
        val children = repository.getAllChildren().first()

        assertEquals(1, children.size)
        assertEquals("John Doe", children[0].fullName)
    }

    @Test
    fun testDeleteChild() = runBlocking {
        val child = Child(
            fullName = "Jane Doe",
            birthDate = "02/02/2016",
            gender = "Female",
            parentUserId = "parent456"
        )

        // Insert dulu
        repository.insertChild(child)

        // Ambil dari database agar dapat id yang benar
        val inserted = repository.getAllChildren().first()
        assertEquals(1, inserted.size) // pastikan sudah ter-insert

        // Hapus menggunakan object dari database (bukan object lama)
        repository.deleteChild(inserted[0])

        // Cek sudah kosong
        val children = repository.getAllChildren().first()
        assertTrue(children.isEmpty())
    }
}