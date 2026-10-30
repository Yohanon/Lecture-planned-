package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chapters")
data class ChapterEntity(
    @PrimaryKey
    val id: String,
    val subject: String, // "PHYSICS", "CHEMISTRY", "MATHEMATICS"
    val name: String,
    val orderIndex: Int,
    val totalLectures: Int,
    val attendedLectures: Int = 0,
    val isModuleReferenceBookDone: Boolean = false,
    val isPyqDone: Boolean = false
) {
    val progressPercentage: Float
        get() = if (totalLectures > 0) {
            (attendedLectures.toFloat() / totalLectures.toFloat()).coerceIn(0f, 1f)
        } else 0f

    val isTestReady: Boolean
        get() = progressPercentage >= 0.70f

    val lecturesRemainingFor70Percent: Int
        get() {
            val target = kotlin.math.ceil(totalLectures * 0.70).toInt()
            return (target - attendedLectures).coerceAtLeast(0)
        }
}
