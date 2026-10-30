package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChapterEntity
import com.example.data.model.Subject
import com.example.ui.components.ChapterCard
import com.example.ui.components.SubjectHeader
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.SlateBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class ChapterFilter(val title: String) {
    ALL("All Chapters"),
    TEST_READY("Ready (≥70%)"),
    NEEDS_LECTURES("Needs Lectures (<70%)"),
    PENDING_MODULE("Pending Module"),
    PENDING_PYQ("Pending PYQ")
}

@Composable
fun SubjectScreen(
    subject: Subject,
    chapters: List<ChapterEntity>,
    onIncrementLecture: (ChapterEntity) -> Unit,
    onDecrementLecture: (ChapterEntity) -> Unit,
    onUpdateLectures: (chapterId: String, attended: Int, total: Int) -> Unit,
    onToggleModule: (ChapterEntity) -> Unit,
    onTogglePyq: (ChapterEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ChapterFilter.ALL) }

    val filteredChapters = chapters.filter { chapter ->
        val matchesSearch = chapter.name.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            ChapterFilter.ALL -> true
            ChapterFilter.TEST_READY -> chapter.isTestReady
            ChapterFilter.NEEDS_LECTURES -> !chapter.isTestReady
            ChapterFilter.PENDING_MODULE -> !chapter.isModuleReferenceBookDone
            ChapterFilter.PENDING_PYQ -> !chapter.isPyqDone
        }
        matchesSearch && matchesFilter
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBlack)
            .testTag("subject_screen_${subject.name.lowercase()}"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Subject Statistics & Header
        item {
            SubjectHeader(
                subject = subject,
                chapters = chapters
            )
        }

        // Search & Filter Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search chapters in ${subject.displayName}...", color = TextMuted) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Search",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_chapter_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedBorderColor = subject.getColor(),
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                // Filter Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ChapterFilter.values()) { filter ->
                        val isSelected = filter == selectedFilter
                        val count = when (filter) {
                            ChapterFilter.ALL -> chapters.size
                            ChapterFilter.TEST_READY -> chapters.count { it.isTestReady }
                            ChapterFilter.NEEDS_LECTURES -> chapters.count { !it.isTestReady }
                            ChapterFilter.PENDING_MODULE -> chapters.count { !it.isModuleReferenceBookDone }
                            ChapterFilter.PENDING_PYQ -> chapters.count { !it.isPyqDone }
                        }

                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    1.dp,
                                    if (isSelected) subject.getColor() else CardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedFilter = filter },
                            color = if (isSelected) subject.getColor().copy(alpha = 0.2f) else CardElevated,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = filter.title,
                                    color = if (isSelected) subject.getColor() else TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "($count)",
                                    color = if (isSelected) subject.getColor() else TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chapters List
        if (filteredChapters.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No chapters found matching filter",
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        } else {
            items(filteredChapters, key = { it.id }) { chapter ->
                ChapterCard(
                    chapter = chapter,
                    accentColor = subject.getColor(),
                    onIncrement = { onIncrementLecture(chapter) },
                    onDecrement = { onDecrementLecture(chapter) },
                    onUpdateLectures = { attended, total ->
                        onUpdateLectures(chapter.id, attended, total)
                    },
                    onToggleModule = { onToggleModule(chapter) },
                    onTogglePyq = { onTogglePyq(chapter) }
                )
            }
        }
    }
}
