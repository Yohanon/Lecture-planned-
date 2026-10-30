package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TestEntity
import com.example.data.model.TestRecommendation
import com.example.ui.components.RetakeTestCard
import com.example.ui.components.TestCard
import com.example.ui.theme.CardBackground
import com.example.ui.theme.CardBorder
import com.example.ui.theme.CardElevated
import com.example.ui.theme.SlateBlack
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TestPlannerScreen(
    recommendedTests: List<TestRecommendation>,
    upcomingTests: List<TestRecommendation>,
    retakeTests: List<TestEntity>,
    onCheckoutTest: (testId: String, scoreNote: String?) -> Unit,
    onRetakeTest: (testId: String) -> Unit,
    onResetTest: (testId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    val tabs = listOf(
        Pair("Recommended", recommendedTests.size),
        Pair("Upcoming", upcomingTests.size),
        Pair("Retake Tests", retakeTests.size)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBlack)
            .testTag("test_planner_screen")
    ) {
        // Top Explanation Banner: 70% Criterion
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        listOf(Color(0xFF38BDF8).copy(alpha = 0.5f), Color(0xFF10B981).copy(alpha = 0.5f))
                    ),
                    RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            color = CardBackground
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Rule Info",
                        tint = Color(0xFF38BDF8),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Recommendation Criterion: 70% Lectures",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "A test is recommended when ≥70% of lectures are attended for every chapter in its syllabus. Completed tests move to 'Retake Tests'.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CardBackground,
            contentColor = Color(0xFF38BDF8),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = when (selectedTab) {
                        0 -> Color(0xFF34D399)
                        1 -> Color(0xFF38BDF8)
                        else -> Color(0xFFA78BFA)
                    }
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, (title, count) ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("test_tab_$index"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = title,
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) TextPrimary else TextSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (selectedTab == index) {
                                            when (index) {
                                                0 -> Color(0xFF065F46)
                                                1 -> Color(0xFF0369A1)
                                                else -> Color(0xFF4C1D95)
                                            }
                                        } else CardElevated
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$count",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == index) TextPrimary else TextMuted
                                )
                            }
                        }
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> {
                // Recommended Tests Tab
                if (recommendedTests.isEmpty()) {
                    EmptyTestState(
                        title = "No Tests Recommended Yet",
                        message = "Attend at least 70% of the lectures in all syllabus chapters for a test to unlock it here. Check 'Upcoming' to see what's close!",
                        icon = Icons.Default.Star,
                        accentColor = Color(0xFF34D399)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Eligible Tests Ready to Take (${recommendedTests.size})",
                                color = Color(0xFF34D399),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(recommendedTests, key = { it.test.id }) { rec ->
                            TestCard(
                                recommendation = rec,
                                onCheckoutTest = onCheckoutTest
                            )
                        }
                    }
                }
            }
            1 -> {
                // Upcoming In-Progress Tests Tab
                if (upcomingTests.isEmpty()) {
                    EmptyTestState(
                        title = "All Tests Completed or Recommended!",
                        message = "You have unlocked all tests in the Arjuna JEE planner.",
                        icon = Icons.Default.AssignmentTurnedIn,
                        accentColor = Color(0xFF38BDF8)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Upcoming Tests in Syllabus (${upcomingTests.size})",
                                color = Color(0xFF38BDF8),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(upcomingTests, key = { it.test.id }) { rec ->
                            TestCard(
                                recommendation = rec,
                                onCheckoutTest = onCheckoutTest
                            )
                        }
                    }
                }
            }
            2 -> {
                // Retake Tests Tab
                if (retakeTests.isEmpty()) {
                    EmptyTestState(
                        title = "No Retake Tests Yet",
                        message = "Once you complete and check out a recommended test, it will automatically move here for revision and retaking!",
                        icon = Icons.Default.Replay,
                        accentColor = Color(0xFFA78BFA)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 14.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            Text(
                                text = "Completed Tests Available to Retake (${retakeTests.size})",
                                color = Color(0xFFA78BFA),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        items(retakeTests, key = { it.id }) { test ->
                            RetakeTestCard(
                                test = test,
                                onRetake = onRetakeTest,
                                onReset = onResetTest
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyTestState(
    title: String,
    message: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f))
                    .border(1.dp, accentColor.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = message,
                color = TextSecondary,
                fontSize = 13.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    }
}
