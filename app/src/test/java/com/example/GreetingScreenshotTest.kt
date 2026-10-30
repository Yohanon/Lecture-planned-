package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.local.ChapterEntity
import com.example.data.model.Subject
import com.example.ui.components.ChapterCard
import com.example.ui.components.SubjectHeader
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun greeting_screenshot() {
        val sampleChapter = ChapterEntity(
            id = "phy_units",
            subject = "PHYSICS",
            name = "Units and Measurements",
            orderIndex = 1,
            totalLectures = 8,
            attendedLectures = 6,
            isModuleReferenceBookDone = true,
            isPyqDone = false
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                SubjectHeader(
                    subject = Subject.PHYSICS,
                    chapters = listOf(sampleChapter)
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
    }
}
