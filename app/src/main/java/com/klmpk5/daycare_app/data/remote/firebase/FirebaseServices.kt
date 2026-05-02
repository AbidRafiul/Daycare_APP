package com.klmpk5.daycare_app.data.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_app.data.remote.model.ChildRemoteDto
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    // --- Child Operations ---
    suspend fun getChildrenByParent(parentUserId: String): List<ChildRemoteDto> {
        val snapshot = db.collection("children")
            .whereEqualTo("parentUserId", parentUserId)
            .get()
            .await()
        return snapshot.documents.mapNotNull { it.toObject(ChildRemoteDto::class.java) }
    }

    suspend fun addChild(child: ChildRemoteDto) {
        db.collection("children").document(child.childId).set(child).await()
    }

    // --- Sisakan ruang untuk operasi WeeklyPlan dan DailyScore selanjutnya ---
}