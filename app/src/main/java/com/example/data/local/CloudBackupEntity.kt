package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloud_backups")
data class CloudBackupEntity(
    @PrimaryKey
    val backupId: String,
    val username: String,
    val googleEmail: String?,
    val chaptersJson: String,
    val testsJson: String,
    val lastSyncedAt: Long,
    val deviceName: String = "Android Device"
)
