package com.klmpk5.daycare_app.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_app.data.local.entities.WeeklyPlan

class WeeklyPlanViewModel : ViewModel() {
    var weeklyPlans = mutableStateOf<List<WeeklyPlan>>(emptyList())
    var isLoading = mutableStateOf(true)

    fun loadWeeklyPlans(childId: String) {
        isLoading.value = true
        val db = FirebaseFirestore.getInstance()

        // Asumsi nama collection di Firebase adalah "weekly_plans"
        db.collection("weekly_plans")
            .whereEqualTo("childId", childId)
            .get()
            .addOnSuccessListener { documents ->
                val listData = mutableListOf<WeeklyPlan>()
                for (doc in documents) {
                    // Ubah setiap dokumen yang ketemu jadi model WeeklyPlan
                    val plan = doc.toObject(WeeklyPlan::class.java)
                    listData.add(plan)
                }
                weeklyPlans.value = listData // Masukkan daftar jadwal ke variabel UI
                isLoading.value = false      // MATIKAN LOADING SAAT SUKSES
            }
            .addOnFailureListener { exception ->
                isLoading.value = false      // MATIKAN LOADING SAAT GAGAL/ERROR
            }
    }
}