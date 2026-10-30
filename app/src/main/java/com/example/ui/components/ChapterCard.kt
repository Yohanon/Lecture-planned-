package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ChapterCard(
    chapter: ChapterEntity,
    accentColor: Color,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onUpdateLectures: (attended: Int, total: Int) -> Unit,
    onToggleModule: () -> Unit,
    onTogglePyq: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showEditDialog by remember { mutableStateOf(false) }

    val percentage = (chapter.progressPercentage * 100).toInt()
    val isReady = chapter.isTestReady

    val borderColor by animateColorAsState(
        targetValue = if (isReady) Color(0xFF10B981).copy(alpha = 0.6f) else CardBorder,
        label = "borderColor"
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chapter_card_${chapter.id}")
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Chapter Index & Name + Edit Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${chapter.orderIndex}",
                            color = accentColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = chapter.name,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(
                    onClick = { showEditDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("edit_chapter_${chapter.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Lecture Target",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar and 70% threshold indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Lectures: ${chapter.attendedLectures} / ${chapter.totalLectures}",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                // 70% Criterion Badge
                if (isReady) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF064E3B).copy(alpha = 0.7f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Eligible",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Test Ready ($percentage%)",
                            color = Color(0xFF34D399),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF451A03).copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$percentage% (Need ${chapter.lecturesRemainingFor70Percent} to 70%)",
                            color = Color(0xFFFBBF24),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Progress Bar
            Box(modifier = Modifier.fillMaxWidth()) {
                LinearProgressIndicator(
                    progress = { chapter.progressPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (isReady) Color(0xFF10B981) else accentColor,
                    trackColor = CardElevated
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stepper and Checklists
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Lecture Attended Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardElevated)
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        enabled = chapter.attendedLectures > 0,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("dec_lecture_${chapter.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Lecture",
                            tint = if (chapter.attendedLectures > 0) TextPrimary else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = "${chapter.attendedLectures}",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp)
                    )

                    IconButton(
                        onClick = onIncrement,
                        enabled = chapter.attendedLectures < chapter.totalLectures,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("inc_lecture_${chapter.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Lecture",
                            tint = if (chapter.attendedLectures < chapter.totalLectures) accentColor else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Checklists Section as explicitly requested: "Module/reference book" and "Pyq"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Checklist 1: Module/reference book
                    ChecklistChip(
                        label = "Module / Ref",
                        icon = Icons.Default.MenuBook,
                        isChecked = chapter.isModuleReferenceBookDone,
                        activeColor = Color(0xFF38BDF8),
                        onToggle = onToggleModule,
                        testTag = "module_check_${chapter.id}"
                    )

                    // Checklist 2: Pyq
                    ChecklistChip(
                        label = "PYQ",
                        icon = Icons.Default.Quiz,
                        isChecked = chapter.isPyqDone,
                        activeColor = Color(0xFFA78BFA),
                        onToggle = onTogglePyq,
                        testTag = "pyq_check_${chapter.id}"
                    )
                }
            }
        }
    }

    if (showEditDialog) {
        EditChapterLecturesDialog(
            chapter = chapter,
            onDismiss = { showEditDialog = false },
            onConfirm = { attended, total ->
                onUpdateLectures(attended, total)
                showEditDialog = false
            }
        )
    }
}

@Composable
private fun ChecklistChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isChecked: Boolean,
    activeColor: Color,
    onToggle: () -> Unit,
    testTag: String
) {
    val bgColor = if (isChecked) activeColor.copy(alpha = 0.2f) else CardElevated
    val textColor = if (isChecked) activeColor else TextSecondary
    val borderCol = if (isChecked) activeColor.copy(alpha = 0.6f) else Color.Transparent

    Row(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderCol, RoundedCornerShape(8.dp))
            .clickable { onToggle() }
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isChecked) Icons.Default.Check else icon,
            contentDescription = label,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun EditChapterLecturesDialog(
    chapter: ChapterEntity,
    onDismiss: () -> Unit,
    onConfirm: (attended: Int, total: Int) -> Unit
) {
    var attendedText by remember { mutableStateOf(chapter.attendedLectures.toString()) }
    var totalText by remember { mutableStateOf(chapter.totalLectures.toString()) }
    var errorText by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Edit Lectures for ${chapter.name}",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Update the attended count or target total lectures for this chapter.",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                OutlinedTextField(
                    value = attendedText,
                    onValueChange = {
                        attendedText = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Attended Lectures") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_attended_input")
                )

                OutlinedTextField(
                    value = totalText,
                    onValueChange = {
                        totalText = it.filter { char -> char.isDigit() }
                    },
                    label = { Text("Total Planned Lectures") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_total_input")
                )

                errorText?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val attended = attendedText.toIntOrNull()
                    val total = totalText.toIntOrNull()
                    if (attended == null || total == null || total <= 0) {
                        errorText = "Please enter valid lecture counts (Total > 0)"
                    } else if (attended > total) {
                        errorText = "Attended lectures cannot exceed total planned lectures"
                    } else {
                        onConfirm(attended, total)
                    }
                },
                modifier = Modifier.testTag("dialog_save_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = CardBackground,
        shape = RoundedCornerShape(20.dp)
    )
}
