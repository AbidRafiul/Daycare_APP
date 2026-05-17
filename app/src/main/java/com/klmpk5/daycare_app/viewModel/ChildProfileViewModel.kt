package com.klmpk5.daycare_app.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_app.data.local.entities.Child

class ChildProfileViewModel : ViewModel() {
    var childData = mutableStateOf<Child?>(null) // Sesuaikan nama model datamu
    var isLoading = mutableStateOf(true)

    fun loadProfileByParent(parentUid: String, parentEmail: String? = null) {
        isLoading.value = true
        val db = FirebaseFirestore.getInstance() // Jangan lupa import

        db.collection("children")
            .whereEqualTo("parentUserId", parentUid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doc = documents.documents[0]
                    val child = doc.toObject(Child::class.java)?.withDocumentId(doc.id)
                    childData.value = child
                    isLoading.value = false
                } else {
                    loadProfileByParentEmail(db, parentEmail)
                }
            }
            .addOnFailureListener { exception ->
                // Tulis log error di sini kalau mau
                isLoading.value = false // MATIKAN LOADING SAAT GAGAL/ERROR
            }
    }

    private fun loadProfileByParentEmail(db: FirebaseFirestore, parentEmail: String?) {
        val normalizedEmail = parentEmail?.trim()?.lowercase()

        if (normalizedEmail.isNullOrBlank()) {
            childData.value = null
            isLoading.value = false
            return
        }

        db.collection("children")
            .whereEqualTo("parentEmail", normalizedEmail)
            .get()
            .addOnSuccessListener { documents ->
                childData.value = documents.documents.firstOrNull()?.let { doc ->
                    doc.toObject(Child::class.java)?.withDocumentId(doc.id)
                }
                isLoading.value = false
            }
            .addOnFailureListener {
                childData.value = null
                isLoading.value = false
            }
    }

    private fun Child.withDocumentId(documentId: String): Child {
        return if (childId.isBlank()) copy(childId = documentId) else this
    }
}
