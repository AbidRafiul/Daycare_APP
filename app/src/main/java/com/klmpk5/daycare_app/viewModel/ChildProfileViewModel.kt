package com.klmpk5.daycare_app.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_app.data.local.entities.Child

class ChildProfileViewModel : ViewModel() {
    var childData = mutableStateOf<Child?>(null) // Sesuaikan nama model datamu
    var isLoading = mutableStateOf(true)

    fun loadProfileByParent(parentUid: String) {
        isLoading.value = true
        val db = FirebaseFirestore.getInstance() // Jangan lupa import

        db.collection("children")
            .whereEqualTo("parentUserId", parentUid)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Ambil dokumen pertama dan ubah jadi model Child
                    val child = documents.documents[0].toObject(Child::class.java)
                    childData.value = child
                } else {
                    childData.value = null // Kalau datanya kosong
                }
                isLoading.value = false // MATIKAN LOADING SAAT SUKSES
            }
            .addOnFailureListener { exception ->
                // Tulis log error di sini kalau mau
                isLoading.value = false // MATIKAN LOADING SAAT GAGAL/ERROR
            }
    }
}