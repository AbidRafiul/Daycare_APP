package com.klmpk5.daycare_app.repository

import com.klmpk5.daycare_app.data.local.dao.ChildDao
import com.klmpk5.daycare_app.data.remote.firebase.FirebaseService
import com.klmpk5.daycare_app.data.local.entities.Child
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChildRepository(
    private val childDao: ChildDao,
    private val firebaseService: FirebaseService
) {
    // 1. MEMBACA DATA LOKAL (Digunakan oleh UI/ViewModel untuk ditampilkan ke layar)
    fun getAllChildrenLocal(): Flow<List<Child>> {
        return childDao.getAllChildren()
    }

    // 2. SINKRONISASI DARI FIREBASE KE ROOM (Biasanya dipanggil saat pull-to-refresh / buka aplikasi)
    suspend fun syncChildrenFromRemote(parentUserId: String, parentEmail: String? = null) {
        withContext(Dispatchers.IO) { // Jalankan di background thread
            try {
                // Ambil data dari Firebase
                val remoteChildren = firebaseService.getChildrenForParent(parentUserId, parentEmail)

                // Convert DTO ke Room Entity (Perhatikan: fungsi toEntity() dipanggil di sini!)
                val localChildren = remoteChildren.map { it.toEntity() }

                // Simpan ke Room Database
                localChildren.forEach { child ->
                    childDao.insertChild(child)
                }
            } catch (e: Exception) {
                // Tangkap error jika internet mati atau akses ditolak
                e.printStackTrace()
            }
        }
    }

    // 3. MENAMBAH ANAK BARU (Khusus Admin)
    suspend fun addChild(childEntity: Child) {
        withContext(Dispatchers.IO) {
            try {
                // Simpan ke Room dulu agar UI langsung update (Optimistic UI)
                childDao.insertChild(childEntity)

                // Konversi dari Room Entity ke Firebase DTO secara manual
                val remoteDto = com.klmpk5.daycare_app.data.remote.model.ChildRemoteDto(
                    childId = childEntity.childId,
                    fullName = childEntity.fullName,
                    birthDate = childEntity.birthDate,
                    gender = childEntity.gender,
                    parentUserId = childEntity.parentUserId,
                    parentEmail = childEntity.parentEmail,
                    photoUrl = childEntity.photoUrl,
                    isActive = childEntity.isActive,
                    createdAt = childEntity.createdAt,
                    updatedAt = childEntity.updatedAt
                )

                // Lempar ke Firebase
                firebaseService.addChild(remoteDto)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
