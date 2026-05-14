package com.klmpk5.daycare_app.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_app.data.local.entities.DailyScore

class DailyScoreViewModel : ViewModel() {
    var dailyScores = mutableStateOf<List<DailyScore>>(emptyList())
    var isLoading = mutableStateOf(true)

    fun loadDailyScores(childId: String) {
        isLoading.value = true
        val db = FirebaseFirestore.getInstance()

        // Asumsi nama collection di Firebase adalah "daily_scores"
        db.collection("daily_scores")
            .whereEqualTo("childId", childId)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<DailyScore>()
                for (doc in documents) {
                    // Ubah setiap dokumen yang ketemu jadi model DailyScore
                    val score = doc.toObject(DailyScore::class.java)
                    listData.add(score)
                }
                dailyScores.value = listData // Masukkan daftar nilai ke variabel UI
                isLoading.value = false      // MATIKAN LOADING SAAT SUKSES
            }
            .addOnFailureListener { exception ->
                isLoading.value = false      // MATIKAN LOADING SAAT GAGAL/ERROR
            }
    }
}