package com.klmpk5.daycare_app.data.remote.model

data class DailyScoreRemoteDto(
    val scoreId: String = "",
    val childId: String = "",
    val date: String = "",
    val activityName: String = "",
    val score: String = "",
    val notes: String = ""
)