package com.example.data.model

import com.example.data.local.ChapterEntity
import com.example.data.local.TestEntity

data class TestRecommendation(
    val test: TestEntity,
    val syllabusChapters: List<ChapterEntity>,
    val readyChaptersCount: Int,
    val totalChaptersCount: Int,
    val isFullyRecommended: Boolean,
    val missingLecturesInfo: List<ChapterMissingInfo>
)

data class ChapterMissingInfo(
    val chapterName: String,
    val subject: String,
    val attended: Int,
    val total: Int,
    val neededFor70Percent: Int
)

data class RevisionMatchRecommendation(
    val test: TestEntity,
    val matchedChapters: List<String>,
    val otherSyllabusChapters: List<String>,
    val matchScore: Float, // percentage or ranking score
    val isBestMatch: Boolean,
    val isFromRetake: Boolean
)
