package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChapterEntity
import com.example.data.model.Subject
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SubjectHeader(
    subject: Subject,
    chapters: List<ChapterEntity>,
    modifier: Modifier = Modifier
) {
    val totalLectures = chapters.sumOf { it.totalLectures }
    val attendedLectures = chapters.sumOf { it.attendedLectures }
    val readyChapters = chapters.count { it.isTestReady }
    val totalChapters = chapters.size
    val moduleDone = chapters.count { it.isModuleReferenceBookDone }
    val pyqDone = chapters.count { it.isPyqDone }

    val percentage = if (totalLectures > 0) {
        ((attendedLectures.toFloat() / totalLectures.toFloat()) * 100).toInt()
    } else 0

    val subjectIcon = when (subject) {
        Subject.PHYSICS -> Icons.Default.Speed
        Subject.CHEMISTRY -> Icons.Default.Science
        Subject.MATHEMATICS -> Icons.Default.Calculate
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp,
                Brush.horizontalGradient(
                    listOf(
                        subject.getColor().copy(alpha = 0.5f),
                        CardBorder
                    )
                ),
                RoundedCornerShape(20.dp)
            ),
        shape = RoundedCornerShape(20.dp),
        color = CardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Subject Title & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(subject.getColor().copy(alpha = 0.15f))
                            .border(1.dp, subject.getColor().copy(alpha = 0.4f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = subjectIcon,
                            contentDescription = subject.displayName,
                            tint = subject.getColor(),
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = subject.displayName,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$totalChapters Chapters • Arjuna JEE 2.0",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                // Overall % Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(subject.getColor().copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$percentage% Done",
                        color = subject.getColor(),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Overall Progress bar
            LinearProgressIndicator(
                progress = { if (totalLectures > 0) attendedLectures.toFloat() / totalLectures.toFloat() else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = subject.getColor(),
                trackColor = CardElevated
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Key Stats in Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Lectures",
                    value = "$attendedLectures/$totalLectures",
                    color = subject.getColor()
                )
                StatItem(
                    label = "Test Ready (≥70%)",
                    value = "$readyChapters/$totalChapters",
                    color = Color(0xFF34D399)
                )
                StatItem(
                    label = "Module / Ref",
                    value = "$moduleDone/$totalChapters",
                    color = Color(0xFF38BDF8)
                )
                StatItem(
                    label = "PYQ Done",
                    value = "$pyqDone/$totalChapters",
                    color = Color(0xFFA78BFA)
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = TextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
