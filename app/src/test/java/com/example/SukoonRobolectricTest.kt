package com.example

import android.content.Context
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AzkarEntity
import com.example.data.local.SukoonDatabase
import com.example.data.preferences.PreferencesManager
import com.example.data.repository.SukoonRepository
import com.example.domain.SceneThemeIds
import com.example.domain.SceneThemeRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class SukoonRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun `scene transformation engine divides 100 beads into 10 progressive stages`() {
        val stage0 = SceneThemeRegistry.calculateStageProgress(0, 100, SceneThemeIds.DEAD_TREE_TO_FRUITFUL)
        val stage1 = SceneThemeRegistry.calculateStageProgress(10, 100, SceneThemeIds.DEAD_TREE_TO_FRUITFUL)
        val stage5 = SceneThemeRegistry.calculateStageProgress(50, 100, SceneThemeIds.DARKNESS_TO_SUNRISE)
        val stage10 = SceneThemeRegistry.calculateStageProgress(100, 100, SceneThemeIds.DRY_DESERT_TO_GARDEN)

        assertEquals(0, stage0.stage)
        assertEquals(0.0f, stage0.normalizedProgress, 0.001f)
        assertEquals(1, stage1.stage)
        assertEquals(0.1f, stage1.normalizedProgress, 0.001f)
        assertEquals(5, stage5.stage)
        assertEquals(0.5f, stage5.normalizedProgress, 0.001f)
        assertEquals(10, stage10.stage)
        assertEquals(1.0f, stage10.normalizedProgress, 0.001f)
    }

    @Test
    fun `room persistence saves bead progress, daily zikr, and completed sessions`() = runTest {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, SukoonDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        val prefs = PreferencesManager(context)
        val repo = SukoonRepository(context, db.azkarDao(), prefs)

        val insertedId = repo.upsertZikr(
            AzkarEntity(
                title = "Test Zikr",
                arabicText = "سُبْحَانَ اللَّهِ",
                urduTranslation = "اللہ پاک ہے",
                englishTranslation = "Glory be to Allah",
                targetCount = 33,
                themeId = SceneThemeIds.WILTED_TO_BLOOMING_FLOWER,
                isDailyZikr = true,
                isPublished = true
            )
        )

        repo.saveBeadProgress(insertedId, 12, incrementLifetime = true)
        val loaded = repo.getZikrById(insertedId)!!
        assertEquals(12, loaded.currentCount)

        repo.recordSession(loaded, completedCount = 33, durationSeconds = 45L, isTargetCompleted = true)
        val sessions = repo.completedSessions.first()
        assertEquals(1, sessions.size)
        assertEquals(33, sessions.first().completedCount)
        assertTrue(sessions.first().isTargetCompleted)

        // Verify PIN hashing
        prefs.setAdminPasscode("4321")
        assertTrue(prefs.verifyAdminPasscode("4321"))
        assertFalse(prefs.verifyAdminPasscode("9999"))

        db.close()
    }

    @Test
    fun `tasbeeh bead increments count by exactly 1 and updates stage badge`() {
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule.onAllNodesWithTag("start_todays_zikr_button")
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithTag("start_todays_zikr_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule.onAllNodesWithTag("tasbeeh_bead_button")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("tasbeeh_count_text", useUnmergedTree = true).assertTextEquals("0")
        composeTestRule.onNodeWithTag("tasbeeh_stage_badge").assertTextEquals("Stage 0 / 10")

        repeat(10) {
            composeTestRule.onNodeWithTag("tasbeeh_bead_button").performClick()
        }
        composeTestRule.waitUntil(timeoutMillis = 5000L) {
            composeTestRule.onAllNodesWithTag("tasbeeh_stage_badge")
                .fetchSemanticsNodes().any { node ->
                    node.config.getOrElse(androidx.compose.ui.semantics.SemanticsProperties.Text) { emptyList() }
                        .joinToString("") == "Stage 1 / 10"
                }
        }

        composeTestRule.onNodeWithTag("tasbeeh_count_text", useUnmergedTree = true).assertTextEquals("10")
        composeTestRule.onNodeWithTag("tasbeeh_stage_badge").assertTextEquals("Stage 1 / 10")

        // Pause prevents counting
        composeTestRule.onNodeWithTag("tasbeeh_pause_toggle").performClick()
        composeTestRule.onNodeWithTag("tasbeeh_bead_button").performClick()
        composeTestRule.waitForIdle()

        // Resume shows 10 still
        composeTestRule.onNodeWithTag("tasbeeh_pause_toggle").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("tasbeeh_count_text", useUnmergedTree = true).assertTextEquals("10")
    }
}
