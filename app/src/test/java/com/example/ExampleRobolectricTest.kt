package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.ArjunaDataInitializers
import com.example.data.local.ChapterEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Lecture Planner", appName)
    }

    @Test
    fun `test 70 percent lecture threshold criterion`() {
        // Chapter with 10 lectures: 7 attended = 70% -> Ready
        val readyChapter = ChapterEntity(
            id = "test_1",
            subject = "PHYSICS",
            name = "Units and Measurements",
            orderIndex = 1,
            totalLectures = 10,
            attendedLectures = 7
        )
        assertTrue(readyChapter.isTestReady)
        assertEquals(0, readyChapter.lecturesRemainingFor70Percent)

        // Chapter with 10 lectures: 6 attended = 60% -> Not ready, needs 1
        val notReadyChapter = ChapterEntity(
            id = "test_2",
            subject = "PHYSICS",
            name = "Mathematical Tools",
            orderIndex = 2,
            totalLectures = 10,
            attendedLectures = 6
        )
        assertFalse(notReadyChapter.isTestReady)
        assertEquals(1, notReadyChapter.lecturesRemainingFor70Percent)
    }

    @Test
    fun `verify default chapters and tests loaded`() {
        val defaultChapters = ArjunaDataInitializers.getDefaultChapters()
        val defaultTests = ArjunaDataInitializers.getDefaultTests()

        assertTrue(defaultChapters.isNotEmpty())
        assertTrue(defaultTests.size >= 25)

        val physicsChapters = defaultChapters.filter { it.subject == "PHYSICS" }
        val chemistryChapters = defaultChapters.filter { it.subject == "CHEMISTRY" }
        val mathChapters = defaultChapters.filter { it.subject == "MATHEMATICS" }

        assertTrue(physicsChapters.isNotEmpty())
        assertTrue(chemistryChapters.isNotEmpty())
        assertTrue(mathChapters.isNotEmpty())
    }

    @Test
    fun `test user profile and google account linking state`() {
        val initialProfile = com.example.data.local.UserProfileEntity(
            id = "primary_user",
            username = "Aspirant",
            isLoggedIn = false,
            isGoogleLinked = false
        )
        assertFalse(initialProfile.isLoggedIn)
        assertFalse(initialProfile.isGoogleLinked)

        val loggedInProfile = initialProfile.copy(
            username = "JohnAaron",
            isLoggedIn = true,
            isGoogleLinked = true,
            googleEmail = "johnaaronsiby@gmail.com",
            googleDisplayName = "John Aaron"
        )
        assertTrue(loggedInProfile.isLoggedIn)
        assertTrue(loggedInProfile.isGoogleLinked)
        assertEquals("JohnAaron", loggedInProfile.username)
        assertEquals("johnaaronsiby@gmail.com", loggedInProfile.googleEmail)
    }
}
