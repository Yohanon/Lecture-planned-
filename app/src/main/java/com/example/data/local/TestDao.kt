package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Query("SELECT * FROM tests ORDER BY orderIndex ASC")
    fun getAllTests(): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestById(id: String): TestEntity?

    @Query("SELECT COUNT(*) FROM tests")
    suspend fun getTestCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(tests: List<TestEntity>)

    @Update
    suspend fun updateTest(test: TestEntity)

    @Query("UPDATE tests SET isCompleted = 1, completedAt = :completedAt, scoreNote = :scoreNote WHERE id = :testId")
    suspend fun markTestCompleted(testId: String, completedAt: Long, scoreNote: String?)

    @Query("UPDATE tests SET retakeCount = retakeCount + 1, completedAt = :retakeTimestamp WHERE id = :testId")
    suspend fun recordRetake(testId: String, retakeTimestamp: Long)

    @Query("UPDATE tests SET isCompleted = 0, completedAt = NULL WHERE id = :testId")
    suspend fun resetTestCompletion(testId: String)
}
