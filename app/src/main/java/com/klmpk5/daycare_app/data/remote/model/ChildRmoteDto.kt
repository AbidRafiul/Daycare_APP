package com.klmpk5.daycare_app.data.remote.model

data class ChildRemoteDto(
    val fullName: String,
    val birthDate: String,
    val gender: String,
    val parentUserId: String,
    val photoUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)