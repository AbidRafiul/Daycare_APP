package com.klmpk5.daycare_app.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

data class ChildFireData(
    val childId: String = "",
    val fullName: String = "",
    val gender: String = "",
    val birthDate: String = "",
    val parentUserId: String = ""
)

data class WeeklyFirePlan(
    val planId: String = "",
    val description: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val imageUrl: String? = null,
    val childId: String = ""
)

data class DailyFireScore(
    val scoreId: String = "",
    val childId: String = "",
    val activityName: String = "",
    val date: String = "",
    val score: String = "",
    val notes: String = ""
)

class ChildViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    var childData by mutableStateOf<ChildFireData?>(null)
        private set
    var weeklyPlan by mutableStateOf<WeeklyFirePlan?>(null)
        private set
    var dailyScore by mutableStateOf<DailyFireScore?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf("")
        private set

    fun getFullChildData(parentUid: String) {
        isLoading = true
        errorMessage = ""

        db.collection("children")
            .whereEqualTo("parentUserId", parentUid)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    val child = result.documents[0].toObject(ChildFireData::class.java)
                    childData = child

                    if (child != null && child.childId.isNotEmpty()) {
                        fetchWeeklyPlan(child.childId)
                        fetchDailyScore(child.childId)
                    } else {
                        isLoading = false
                    }
                } else {
                    isLoading = false
                    errorMessage = "Belum ada data anak untuk akun ini."
                }
            }
            .addOnFailureListener { e ->
                isLoading = false
                errorMessage = "Gagal mengambil data: ${e.message}"
            }
    }

    private fun fetchWeeklyPlan(childId: String) {
        db.collection("weekly_plans")
            .whereEqualTo("childId", childId)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    weeklyPlan = result.documents[0].toObject(WeeklyFirePlan::class.java)
                }
            }
    }

    private fun fetchDailyScore(childId: String) {
        db.collection("daily_scores")
            .whereEqualTo("childId", childId)
            .limit(1)
            .get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    dailyScore = result.documents[0].toObject(DailyFireScore::class.java)
                }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }
}