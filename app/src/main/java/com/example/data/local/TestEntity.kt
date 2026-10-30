package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val batchName: String = "Arjuna JEE 2.0 2027",
    val dateStr: String,
    val testType: String, // "Part Test", "Full Test"
    val testPattern: String, // "Short Test", "JEE Main", "JEE Advanced", etc.
    val orderIndex: Int,
    val chaptersListCsv: String, // Comma-separated chapter names included in syllabus
    val isCompleted: Boolean = false,
    val completedAt: Long? = null,
    val retakeCount: Int = 0,
    val scoreNote: String? = null
) {
    fun getSyllabusChapterNames(): List<String> {
        return chaptersListCsv.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
    }
}
