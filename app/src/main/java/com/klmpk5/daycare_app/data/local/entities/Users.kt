package com.klmpk5.daycare_app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val uid: String,

    val fullName: String,
    val email: String,
    val phoneNumber: String? = null,

    // Untuk app parent, default role = parent.
    val role: String = "parent",

    val photoUrl: String? = null,

    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
