package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.ChapterEntity
import com.example.data.local.TestEntity
import com.example.data.local.UserProfileEntity
import com.example.data.model.ChapterMissingInfo
import com.example.data.model.RevisionMatchRecommendation
import com.example.data.model.Subject
import com.example.data.model.TestRecommendation
import com.example.data.repository.LectureRepository
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LecturePlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LectureRepository
    private val networkMonitor = NetworkMonitor(application)

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), networkMonitor.isCurrentlyOnline())

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow<String?>(null)
    val syncStatusMessage: StateFlow<String?> = _syncStatusMessage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LectureRepository(database.chapterDao(), database.testDao(), database.userDao())
        viewModelScope.launch {
            repository.ensureDataInitialized()
        }
    }

    val userProfile: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allChapters: StateFlow<List<ChapterEntity>> = repository.allChapters
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTests: StateFlow<List<TestEntity>> = repository.allTests
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected revision chapters (by chapter name)
    private val _selectedRevisionChapterNames = MutableStateFlow<Set<String>>(emptySet())
    val selectedRevisionChapterNames: StateFlow<Set<String>> = _selectedRevisionChapterNames.asStateFlow()

    // Test recommendations combining chapters and tests
    val testRecommendations: StateFlow<List<TestRecommendation>> = combine(allChapters, allTests) { chapters, tests ->
        val chapterMap = chapters.associateBy { it.name.trim().lowercase() }

        tests.map { test ->
            val syllabusNames = test.getSyllabusChapterNames()
            val matchedChapters = mutableListOf<ChapterEntity>()
            val missingInfoList = mutableListOf<ChapterMissingInfo>()
            var readyCount = 0

            syllabusNames.forEach { sName ->
                val cleanName = sName.trim().lowercase()
                val chapter = chapterMap[cleanName]
                if (chapter != null) {
                    matchedChapters.add(chapter)
                    if (chapter.isTestReady) {
                        readyCount++
                    } else {
                        missingInfoList.add(
                            ChapterMissingInfo(
                                chapterName = chapter.name,
                                subject = chapter.subject,
                                attended = chapter.attendedLectures,
                                total = chapter.totalLectures,
                                neededFor70Percent = chapter.lecturesRemainingFor70Percent
                            )
                        )
                    }
                }
            }

            val totalSyllabusCount = syllabusNames.size
            val isFullyRecommended = totalSyllabusCount > 0 && readyCount == totalSyllabusCount

            TestRecommendation(
                test = test,
                syllabusChapters = matchedChapters,
                readyChaptersCount = readyCount,
                totalChaptersCount = totalSyllabusCount,
                isFullyRecommended = isFullyRecommended,
                missingLecturesInfo = missingInfoList
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Recommended tests: Not completed & 70% criteria met for all syllabus chapters
    val eligibleRecommendedTests: StateFlow<List<TestRecommendation>> = testRecommendations.combine(allTests) { recs, _ ->
        recs.filter { !it.test.isCompleted && it.isFullyRecommended }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // In-progress upcoming tests: Not completed & not all chapters at 70%
    val upcomingTests: StateFlow<List<TestRecommendation>> = testRecommendations.combine(allTests) { recs, _ ->
        recs.filter { !it.test.isCompleted && !it.isFullyRecommended }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Retake tests: Completed tests
    val retakeTests: StateFlow<List<TestEntity>> = allTests.combine(allChapters) { tests, _ ->
        tests.filter { it.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Revision matches based on selected chapters and retake tests
    val revisionMatches: StateFlow<List<RevisionMatchRecommendation>> = combine(
        selectedRevisionChapterNames,
        allTests
    ) { selectedNames, tests ->
        if (selectedNames.isEmpty()) {
            return@combine emptyList()
        }

        val completedTests = tests.filter { it.isCompleted }
        val pool = if (completedTests.isNotEmpty()) completedTests else tests // fallback preview if none completed
        val isFromRetake = completedTests.isNotEmpty()

        val normalizedSelected = selectedNames.map { it.trim().lowercase() }.toSet()

        val scoredList = pool.mapNotNull { test ->
            val syllabus = test.getSyllabusChapterNames()
            val matched = syllabus.filter { normalizedSelected.contains(it.trim().lowercase()) }
            val other = syllabus.filter { !normalizedSelected.contains(it.trim().lowercase()) }

            if (matched.isNotEmpty()) {
                val matchFraction = matched.size.toFloat() / selectedNames.size.toFloat()
                val precisionFraction = matched.size.toFloat() / syllabus.size.coerceAtLeast(1).toFloat()
                // Combined weighted score
                val score = (matchFraction * 0.65f) + (precisionFraction * 0.35f)

                RevisionMatchRecommendation(
                    test = test,
                    matchedChapters = matched,
                    otherSyllabusChapters = other,
                    matchScore = score,
                    isBestMatch = false,
                    isFromRetake = isFromRetake
                )
            } else null
        }.sortedWith(
            compareByDescending<RevisionMatchRecommendation> { it.matchedChapters.size }
                .thenByDescending { it.matchScore }
        )

        if (scoredList.isEmpty()) {
            emptyList()
        } else {
            // First is best match, next 2-3 are close options (max 4 total: 1 best + 3 close options)
            scoredList.take(4).mapIndexed { index, item ->
                item.copy(isBestMatch = index == 0)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Actions
    fun incrementLecture(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.incrementAttendedLectures(chapter)
        }
    }

    fun decrementLecture(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.decrementAttendedLectures(chapter)
        }
    }

    fun setAttendedLectures(chapterId: String, attended: Int, total: Int) {
        viewModelScope.launch {
            repository.setAttendedLectures(chapterId, attended, total)
        }
    }

    fun updateTotalLectures(chapterId: String, total: Int) {
        viewModelScope.launch {
            repository.updateTotalLectures(chapterId, total)
        }
    }

    fun toggleModuleChecklist(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.toggleModuleChecklist(chapter)
        }
    }

    fun togglePyqChecklist(chapter: ChapterEntity) {
        viewModelScope.launch {
            repository.togglePyqChecklist(chapter)
        }
    }

    fun checkoutTest(testId: String, scoreNote: String? = null) {
        viewModelScope.launch {
            repository.markTestCompleted(testId, scoreNote)
        }
    }

    fun retakeTest(testId: String) {
        viewModelScope.launch {
            repository.retakeTest(testId)
        }
    }

    fun resetTest(testId: String) {
        viewModelScope.launch {
            repository.resetTestCompletion(testId)
        }
    }

    fun toggleRevisionChapter(chapterName: String) {
        val current = _selectedRevisionChapterNames.value.toMutableSet()
        if (current.contains(chapterName)) {
            current.remove(chapterName)
        } else {
            current.add(chapterName)
        }
        _selectedRevisionChapterNames.value = current
    }

    fun clearRevisionChapters() {
        _selectedRevisionChapterNames.value = emptySet()
    }

    fun selectAllRevisionChapters(subject: String, chapters: List<ChapterEntity>) {
        val current = _selectedRevisionChapterNames.value.toMutableSet()
        val subjectChapterNames = chapters.filter { it.subject.equals(subject, ignoreCase = true) }.map { it.name }
        if (current.containsAll(subjectChapterNames)) {
            current.removeAll(subjectChapterNames.toSet())
        } else {
            current.addAll(subjectChapterNames)
        }
        _selectedRevisionChapterNames.value = current
    }

    // --- Authentication & Online Sync Actions ---

    fun loginWithUsername(username: String) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.loginWithUsername(username)
            _isSyncing.value = false
            _syncStatusMessage.value = "Logged in as $username & synced to cloud"
        }
    }

    fun linkGoogleAccount(email: String, displayName: String?, googleId: String?) {
        viewModelScope.launch {
            _isSyncing.value = true
            repository.linkGoogleAccount(email, displayName, googleId)
            _isSyncing.value = false
            _syncStatusMessage.value = "Linked with Google Account ($email)"
        }
    }

    fun unlinkGoogleAccount() {
        viewModelScope.launch {
            repository.unlinkGoogleAccount()
            _syncStatusMessage.value = "Google account unlinked"
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _syncStatusMessage.value = "Logged out"
        }
    }

    fun syncNow() {
        viewModelScope.launch {
            _isSyncing.value = true
            val timestamp = repository.syncToCloud()
            _isSyncing.value = false
            _syncStatusMessage.value = "Synced successfully with cloud"
        }
    }

    fun restoreFromCloud(username: String, email: String?, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            _isSyncing.value = true
            val success = repository.restoreFromCloud(username, email)
            _isSyncing.value = false
            if (success) {
                _syncStatusMessage.value = "Restored progress from cloud"
            } else {
                _syncStatusMessage.value = "No existing cloud backup found for this account"
            }
            onResult(success)
        }
    }

    fun clearSyncMessage() {
        _syncStatusMessage.value = null
    }
}
