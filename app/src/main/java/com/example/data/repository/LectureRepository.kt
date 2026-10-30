package com.example.data.repository

import com.example.data.local.ArjunaDataInitializers
import com.example.data.local.ChapterDao
import com.example.data.local.ChapterEntity
import com.example.data.local.CloudBackupEntity
import com.example.data.local.TestDao
import com.example.data.local.TestEntity
import com.example.data.local.UserDao
import com.example.data.local.UserProfileEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class LectureRepository(
    private val chapterDao: ChapterDao,
    private val testDao: TestDao,
    private val userDao: UserDao
) {
    val allChapters: Flow<List<ChapterEntity>> = chapterDao.getAllChapters()
    val allTests: Flow<List<TestEntity>> = testDao.getAllTests()
    val userProfile: Flow<UserProfileEntity?> = userDao.getUserProfile()

    fun getChaptersForSubject(subject: String): Flow<List<ChapterEntity>> {
        return chapterDao.getChaptersBySubject(subject)
    }

    suspend fun ensureDataInitialized() = withContext(Dispatchers.IO) {
        val chapterCount = chapterDao.getChapterCount()
        if (chapterCount == 0) {
            chapterDao.insertAll(ArjunaDataInitializers.getDefaultChapters())
        }
        val testCount = testDao.getTestCount()
        if (testCount == 0) {
            testDao.insertAll(ArjunaDataInitializers.getDefaultTests())
        }
        val existingUser = userDao.getUserProfileOnce()
        if (existingUser == null) {
            userDao.insertOrUpdateUser(
                UserProfileEntity(
                    id = "primary_user",
                    username = "Aspirant",
                    isLoggedIn = false,
                    isGoogleLinked = false
                )
            )
        }
    }

    suspend fun incrementAttendedLectures(chapter: ChapterEntity) = withContext(Dispatchers.IO) {
        val newAttended = (chapter.attendedLectures + 1).coerceAtMost(chapter.totalLectures)
        chapterDao.updateAttendedLectures(chapter.id, newAttended)
        autoSyncIfLoggedIn()
    }

    suspend fun decrementAttendedLectures(chapter: ChapterEntity) = withContext(Dispatchers.IO) {
        val newAttended = (chapter.attendedLectures - 1).coerceAtLeast(0)
        chapterDao.updateAttendedLectures(chapter.id, newAttended)
        autoSyncIfLoggedIn()
    }

    suspend fun setAttendedLectures(chapterId: String, attended: Int, total: Int) = withContext(Dispatchers.IO) {
        val validAttended = attended.coerceIn(0, total.coerceAtLeast(1))
        chapterDao.updateAttendedLectures(chapterId, validAttended)
        autoSyncIfLoggedIn()
    }

    suspend fun updateTotalLectures(chapterId: String, total: Int) = withContext(Dispatchers.IO) {
        if (total > 0) {
            chapterDao.updateTotalLectures(chapterId, total)
            autoSyncIfLoggedIn()
        }
    }

    suspend fun toggleModuleChecklist(chapter: ChapterEntity) = withContext(Dispatchers.IO) {
        chapterDao.updateModuleBookStatus(chapter.id, !chapter.isModuleReferenceBookDone)
        autoSyncIfLoggedIn()
    }

    suspend fun togglePyqChecklist(chapter: ChapterEntity) = withContext(Dispatchers.IO) {
        chapterDao.updatePyqStatus(chapter.id, !chapter.isPyqDone)
        autoSyncIfLoggedIn()
    }

    suspend fun markTestCompleted(testId: String, scoreNote: String?) = withContext(Dispatchers.IO) {
        testDao.markTestCompleted(
            testId = testId,
            completedAt = System.currentTimeMillis(),
            scoreNote = scoreNote
        )
        autoSyncIfLoggedIn()
    }

    suspend fun retakeTest(testId: String) = withContext(Dispatchers.IO) {
        testDao.recordRetake(testId, System.currentTimeMillis())
        autoSyncIfLoggedIn()
    }

    suspend fun resetTestCompletion(testId: String) = withContext(Dispatchers.IO) {
        testDao.resetTestCompletion(testId)
        autoSyncIfLoggedIn()
    }

    // --- User Profile & Authentication ---

    suspend fun loginWithUsername(username: String) = withContext(Dispatchers.IO) {
        val cleanUsername = username.trim()
        val current = userDao.getUserProfileOnce()
        if (current != null) {
            userDao.insertOrUpdateUser(
                current.copy(
                    username = cleanUsername,
                    isLoggedIn = true
                )
            )
        } else {
            userDao.insertOrUpdateUser(
                UserProfileEntity(
                    id = "primary_user",
                    username = cleanUsername,
                    isLoggedIn = true
                )
            )
        }
        syncToCloud()
    }

    suspend fun linkGoogleAccount(
        email: String,
        displayName: String?,
        googleId: String?
    ) = withContext(Dispatchers.IO) {
        val current = userDao.getUserProfileOnce()
        val updated = (current ?: UserProfileEntity(id = "primary_user")).copy(
            isGoogleLinked = true,
            isLoggedIn = true,
            googleEmail = email,
            googleDisplayName = displayName,
            googleId = googleId
        )
        userDao.insertOrUpdateUser(updated)
        syncToCloud()
    }

    suspend fun unlinkGoogleAccount() = withContext(Dispatchers.IO) {
        userDao.unlinkGoogleAccount()
    }

    suspend fun logout() = withContext(Dispatchers.IO) {
        userDao.logout()
    }

    // --- Online Cloud Sync ---

    suspend fun syncToCloud(): Long = withContext(Dispatchers.IO) {
        val user = userDao.getUserProfileOnce() ?: return@withContext 0L
        val chapters = chapterDao.getAllChapters().first()
        val tests = testDao.getAllTests().first()

        // Serialize chapters
        val chaptersArray = JSONArray()
        chapters.forEach { ch ->
            val obj = JSONObject().apply {
                put("id", ch.id)
                put("attended", ch.attendedLectures)
                put("total", ch.totalLectures)
                put("module", ch.isModuleReferenceBookDone)
                put("pyq", ch.isPyqDone)
            }
            chaptersArray.put(obj)
        }

        // Serialize tests
        val testsArray = JSONArray()
        tests.forEach { t ->
            val obj = JSONObject().apply {
                put("id", t.id)
                put("completed", t.isCompleted)
                put("completedAt", t.completedAt ?: 0L)
                put("retakeCount", t.retakeCount)
                put("scoreNote", t.scoreNote ?: "")
            }
            testsArray.put(obj)
        }

        val syncTime = System.currentTimeMillis()
        val backup = CloudBackupEntity(
            backupId = "backup_${user.username}_${syncTime}",
            username = user.username,
            googleEmail = user.googleEmail,
            chaptersJson = chaptersArray.toString(),
            testsJson = testsArray.toString(),
            lastSyncedAt = syncTime
        )
        userDao.saveCloudBackup(backup)
        userDao.updateSyncTime("primary_user", syncTime)
        syncTime
    }

    suspend fun restoreFromCloud(username: String, email: String?): Boolean = withContext(Dispatchers.IO) {
        val latestBackup = userDao.getLatestCloudBackup(username, email) ?: return@withContext false

        try {
            // Restore chapters
            val chaptersArray = JSONArray(latestBackup.chaptersJson)
            for (i in 0 until chaptersArray.length()) {
                val obj = chaptersArray.getJSONObject(i)
                val id = obj.getString("id")
                val attended = obj.getInt("attended")
                val total = obj.getInt("total")
                val module = obj.getBoolean("module")
                val pyq = obj.getBoolean("pyq")

                chapterDao.updateAttendedLectures(id, attended)
                chapterDao.updateTotalLectures(id, total)
                chapterDao.updateModuleBookStatus(id, module)
                chapterDao.updatePyqStatus(id, pyq)
            }

            // Restore tests
            val testsArray = JSONArray(latestBackup.testsJson)
            for (i in 0 until testsArray.length()) {
                val obj = testsArray.getJSONObject(i)
                val id = obj.getString("id")
                val completed = obj.getBoolean("completed")
                val completedAt = obj.getLong("completedAt")
                val retakeCount = obj.getInt("retakeCount")
                val scoreNote = obj.getString("scoreNote").ifBlank { null }

                if (completed) {
                    testDao.markTestCompleted(id, completedAt, scoreNote)
                } else {
                    testDao.resetTestCompletion(id)
                }
            }

            userDao.updateSyncTime("primary_user", latestBackup.lastSyncedAt)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun autoSyncIfLoggedIn() {
        val user = userDao.getUserProfileOnce()
        if (user?.isLoggedIn == true && user.isOnlineSyncEnabled) {
            syncToCloud()
        }
    }
}
