package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChapterDao {
    @Query("SELECT * FROM chapters ORDER BY orderIndex ASC")
    fun getAllChapters(): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE subject = :subject ORDER BY orderIndex ASC")
    fun getChaptersBySubject(subject: String): Flow<List<ChapterEntity>>

    @Query("SELECT * FROM chapters WHERE id = :id LIMIT 1")
    suspend fun getChapterById(id: String): ChapterEntity?

    @Query("SELECT COUNT(*) FROM chapters")
    suspend fun getChapterCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(chapters: List<ChapterEntity>)

    @Update
    suspend fun updateChapter(chapter: ChapterEntity)

    @Query("UPDATE chapters SET attendedLectures = :attended WHERE id = :chapterId")
    suspend fun updateAttendedLectures(chapterId: String, attended: Int)

    @Query("UPDATE chapters SET totalLectures = :total WHERE id = :chapterId")
    suspend fun updateTotalLectures(chapterId: String, total: Int)

    @Query("UPDATE chapters SET isModuleReferenceBookDone = :isDone WHERE id = :chapterId")
    suspend fun updateModuleBookStatus(chapterId: String, isDone: Boolean)

    @Query("UPDATE chapters SET isPyqDone = :isDone WHERE id = :chapterId")
    suspend fun updatePyqStatus(chapterId: String, isDone: Boolean)
}
