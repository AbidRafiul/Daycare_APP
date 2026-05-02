package com.klmpk5.daycare_app.data.remote.model

data class WeeklyPlanRemoteDto(
    val planId: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val imageUrl: String? = null
)