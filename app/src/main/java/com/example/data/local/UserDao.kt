package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    fun getUserProfile(userId: String = "primary_user"): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    suspend fun getUserProfileOnce(userId: String = "primary_user"): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserProfileEntity)

    @Query("UPDATE user_profile SET username = :username, isLoggedIn = 1 WHERE id = :userId")
    suspend fun setUsername(userId: String = "primary_user", username: String)

    @Query("UPDATE user_profile SET isGoogleLinked = 1, googleEmail = :email, googleDisplayName = :displayName, googleId = :googleId WHERE id = :userId")
    suspend fun linkGoogleAccount(userId: String = "primary_user", email: String, displayName: String?, googleId: String?)

    @Query("UPDATE user_profile SET isGoogleLinked = 0, googleEmail = NULL, googleDisplayName = NULL, googleId = NULL WHERE id = :userId")
    suspend fun unlinkGoogleAccount(userId: String = "primary_user")

    @Query("UPDATE user_profile SET lastSyncTime = :syncTime WHERE id = :userId")
    suspend fun updateSyncTime(userId: String = "primary_user", syncTime: Long)

    @Query("UPDATE user_profile SET isLoggedIn = 0 WHERE id = :userId")
    suspend fun logout(userId: String = "primary_user")

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveCloudBackup(backup: CloudBackupEntity)

    @Query("SELECT * FROM cloud_backups WHERE username = :username OR googleEmail = :email ORDER BY lastSyncedAt DESC LIMIT 1")
    suspend fun getLatestCloudBackup(username: String, email: String?): CloudBackupEntity?
}
