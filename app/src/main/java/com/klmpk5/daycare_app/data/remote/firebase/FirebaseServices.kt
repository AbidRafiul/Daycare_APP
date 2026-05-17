package com.klmpk5.daycare_app.data.remote.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.klmpk5.daycare_app.data.remote.model.ChildRemoteDto
import com.klmpk5.daycare_app.data.remote.model.DailyScoreRemoteDto
import com.klmpk5.daycare_app.data.remote.model.WeeklyPlanRemoteDto
import com.klmpk5.daycare_app.data.remote.model.UserRemoteDto
import kotlinx.coroutines.tasks.await

class FirebaseService {
    private val db = FirebaseFirestore.getInstance()

    // ==========================================
    // 1. CHILD OPERATIONS
    // ==========================================
    suspend fun getChildrenByParent(parentUserId: String): List<ChildRemoteDto> {
        val snapshot = db.collection("children")
            .whereEqualTo("parentUserId", parentUserId)
            .get()
            .await()

        return snapshot.documents.toChildDtos()
    }

    suspend fun getChildrenByParentEmail(parentEmail: String): List<ChildRemoteDto> {
        val snapshot = db.collection("children")
            .whereEqualTo("parentEmail", parentEmail.trim().lowercase())
            .get()
            .await()

        return snapshot.documents.toChildDtos()
    }

    suspend fun getChildrenForParent(parentUserId: String, parentEmail: String?): List<ChildRemoteDto> {
        val byUid = getChildrenByParent(parentUserId)
        if (byUid.isNotEmpty() || parentEmail.isNullOrBlank()) {
            return byUid
        }

        return getChildrenByParentEmail(parentEmail)
    }

    private fun List<com.google.firebase.firestore.DocumentSnapshot>.toChildDtos(): List<ChildRemoteDto> {
        return mapNotNull { doc ->
            val child = doc.toObject(ChildRemoteDto::class.java)
            child?.childId = doc.id // Otomatis tangkap ID dari dokumen Firebase
            child
        }
    }

    suspend fun addChild(child: ChildRemoteDto) {
        // Jika childId masih kosong, minta Firestore buatkan ID acak yang unik!
        val docRef = if (child.childId.isEmpty()) {
            db.collection("children").document()
        } else {
            db.collection("children").document(child.childId)
        }

        child.childId = docRef.id // Simpan ID-nya ke dalam objek
        docRef.set(child).await() // Lempar ke database
    }

    // ==========================================
    // 2. WEEKLY PLAN OPERATIONS (ADMIN & USER)
    // ==========================================
    suspend fun getAllWeeklyPlans(): List<WeeklyPlanRemoteDto> {
        val snapshot = db.collection("weekly_plans").get().await()

        return snapshot.documents.mapNotNull { doc ->
            val plan = doc.toObject(WeeklyPlanRemoteDto::class.java)
            plan?.planId = doc.id
            plan
        }
    }

    suspend fun addWeeklyPlan(plan: WeeklyPlanRemoteDto) {
        val docRef = if (plan.planId.isEmpty()) db.collection("weekly_plans").document() else db.collection("weekly_plans").document(plan.planId)
        plan.planId = docRef.id
        docRef.set(plan).await()
    }

    // ==========================================
    // 3. DAILY SCORE OPERATIONS
    // ==========================================
    suspend fun getScoresByChild(childId: String): List<DailyScoreRemoteDto> {
        val snapshot = db.collection("daily_scores")
            .whereEqualTo("childId", childId)
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            val score = doc.toObject(DailyScoreRemoteDto::class.java)
            score?.scoreId = doc.id
            score
        }
    }

    suspend fun addScore(score: DailyScoreRemoteDto) {
        val docRef = if (score.scoreId.isEmpty()) db.collection("daily_scores").document() else db.collection("daily_scores").document(score.scoreId)
        score.scoreId = docRef.id
        docRef.set(score).await()
    }

    // ==========================================
    // 4. USER OPERATIONS (untuk login/register)
    // ==========================================

    suspend fun saveUserProfile(user: UserRemoteDto) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

    suspend fun getUserProfile(uid: String): UserRemoteDto? {
        val document = db.collection("users")
            .document(uid)
            .get()
            .await()

        return document.toObject(UserRemoteDto::class.java)
    }

    suspend fun updateUserProfile(user: UserRemoteDto) {
        db.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }
}