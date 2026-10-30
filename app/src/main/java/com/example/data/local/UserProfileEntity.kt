package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: String = "primary_user",
    val username: String = "Student",
    val isLoggedIn: Boolean = false,
    val isGoogleLinked: Boolean = false,
    val googleEmail: String? = null,
    val googleDisplayName: String? = null,
    val googleId: String? = null,
    val googlePhotoUrl: String? = null,
    val lastSyncTime: Long? = null,
    val isOnlineSyncEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
