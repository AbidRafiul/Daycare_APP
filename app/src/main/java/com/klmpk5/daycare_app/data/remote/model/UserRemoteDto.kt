package com.klmpk5.daycare_app.data.remote.model

data class UserRemoteDto(
    var uid: String = "",
    var fullName: String = "",
    var email: String = "",
    var phoneNumber: String? = null,
    var role: String = "parent",
    var photoUrl: String? = null,
    var createdAt: Long = System.currentTimeMillis(),
    var updatedAt: Long = System.currentTimeMillis()
)
