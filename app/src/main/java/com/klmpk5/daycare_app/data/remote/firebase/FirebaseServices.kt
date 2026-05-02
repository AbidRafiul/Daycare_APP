package com.klmpk5.daycare_app.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_app.data.local.entities.Child
import com.klmpk5.daycare_app.data.remote.model.ChildRemoteDto
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    // Menambahkan Child ke Firebase
    suspend fun addChild(child: ChildRemoteDto) {
        db.collection("children")
            .add(child)
            .await()
    }

    // Mendapatkan Child berdasarkan parentUserId
    suspend fun getChildrenByParent(parentUserId: String): List<ChildRemoteDto> {
        val querySnapshot = db.collection("children")
            .whereEqualTo("parentUserId", parentUserId)
            .get()
            .await()

        return querySnapshot.documents.mapNotNull {
            it.toObject(ChildRemoteDto::class.java)
        }
    }

    // Menyinkronkan data lokal ke Firebase
    suspend fun syncChildrenToFirebase(children: List<ChildRemoteDto>) {
        children.forEach { child ->
            addChild(child)
        }
    }
}