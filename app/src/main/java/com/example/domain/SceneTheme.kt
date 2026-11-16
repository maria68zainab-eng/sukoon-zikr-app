package com.example.domain

import androidx.compose.ui.graphics.Color

object SceneThemeIds {
    const val DEAD_TREE_TO_FRUITFUL = "DEAD_TREE_TO_FRUITFUL"
    const val DARKNESS_TO_SUNRISE = "DARKNESS_TO_SUNRISE"
    const val WILTED_TO_BLOOMING_FLOWER = "WILTED_TO_BLOOMING_FLOWER"
    const val DRY_DESERT_TO_GARDEN = "DRY_DESERT_TO_GARDEN"
}

data class SceneThemeSpec(
    val id: String,
    val displayName: String,
    val shortSubtitle: String,
    val description: String,
    val previewColors: List<Color>,
    val stageDescriptions: List<String>
)

data class StageProgress(
    val currentCount: Int,
    val targetCount: Int,
    val normalizedProgress: Float,
    val stage: Int,
    val totalStages: Int = 10,
    val stageDescription: String
)

object SceneThemeRegistry {
    val themes: List<SceneThemeSpec> = listOf(
        SceneThemeSpec(
            id = SceneThemeIds.DEAD_TREE_TO_FRUITFUL,
            displayName = "Barren Tree to Fruitful Garden",
            shortSubtitle = "From dry branches to shaded sanctuary",
            description = "Begins with a dry, leafless tree in quiet dusk. With every bead, tender buds, lush foliage, blossoms, sunlight, and ripe fruit emerge.",
            previewColors = listOf(Color(0xFF1D242B), Color(0xFF2D5944), Color(0xFFD8B467)),
            stageDescriptions = listOf(
                "Silent winter branches under twilight",
                "First softening mist on the horizon",
                "Gentle warmth touches dry earth",
                "Tender green buds awaken along boughs",
                "Young leaves unfurl in soft light",
                "Lush green canopy spreads shade",
                "Delicate blossoms open among leaves",
                "Golden sunlight filters through the garden",
                "Abundant fruit ripens on every branch",
                "Sunlit orchard in serene fullness",
                "Garden of tranquil gratitude"
            )
        ),
        SceneThemeSpec(
            id = SceneThemeIds.DARKNESS_TO_SUNRISE,
            displayName = "Darkness to Sunrise",
            shortSubtitle = "From clouded night to golden dawn",
            description = "Begins beneath heavy clouds and fading night. Progressively clears the sky, softens stars, and ushers in a calm golden sunrise.",
            previewColors = listOf(Color(0xFF0A1120), Color(0xFF3E4B7A), Color(0xFFF3B76B)),
            stageDescriptions = listOf(
                "Deep midnight beneath drifting clouds",
                "Clouds gently part across quiet hills",
                "Stars soften in pre-dawn stillness",
                "A violet thread of light graces the east",
                "Warm rose hues kiss distant peaks",
                "First golden rays crest the horizon",
                "Sunlight spills softly into the valley",
                "Morning mist glows over still water",
                "Radiant clear sky fills the expanse",
                "Peaceful dawn bathes the earth",
                "Full light of tranquil morning"
            )
        ),
        SceneThemeSpec(
            id = SceneThemeIds.WILTED_TO_BLOOMING_FLOWER,
            displayName = "Wilted Flower to Full Bloom",
            shortSubtitle = "From drooping stem to radiant lotus",
            description = "A parched, bowed blossom gradually drinks in morning dew, lifts its stem, unfolds fresh leaves, and opens in luminous bloom.",
            previewColors = listOf(Color(0xFF231F29), Color(0xFF3A5F4D), Color(0xFFE69FA2)),
            stageDescriptions = listOf(
                "Bowed stem resting in cool twilight",
                "First gentle dew settles on dry petals",
                "Nourished roots revive the stem",
                "Stem rises steadily toward the light",
                "Fresh emerald leaves unfurl outward",
                "Inner bud swells with soft color",
                "Outer petals begin gently parting",
                "Dewdrops sparkle on unfolding layers",
                "Warm radiance surrounds the blossom",
                "Full petal symmetry in gentle glow",
                "Radiant flower resting in peace"
            )
        ),
        SceneThemeSpec(
            id = SceneThemeIds.DRY_DESERT_TO_GARDEN,
            displayName = "Dry Desert to Green Garden",
            shortSubtitle = "From arid dunes to flowing oasis",
            description = "Vast barren sands give way to a clear spring, meandering streams, swaying palms, wildflowers, and verdant meadow.",
            previewColors = listOf(Color(0xFF3B2C20), Color(0xFF387A73), Color(0xFF5CA36E)),
            stageDescriptions = listOf(
                "Still barren dunes under silent dusk",
                "Cool breeze softens arid horizons",
                "A quiet spring emerges in the sand",
                "Clear water begins winding through earth",
                "Verdant grass lines the stream banks",
                "Oasis pools shimmer beneath turquoise sky",
                "Palm and olive saplings take root",
                "Wildflowers bloom beside flowing water",
                "Shade trees flourish across the meadow",
                "Lush paradise garden bathed in warmth",
                "Serene oasis of flowing stillness"
            )
        )
    )

    fun getTheme(themeId: String): SceneThemeSpec {
        return themes.find { it.id == themeId } ?: themes.first()
    }

    fun calculateStageProgress(
        currentCount: Int,
        targetCount: Int,
        themeId: String
    ): StageProgress {
        val safeTarget = targetCount.coerceAtLeast(1)
        val clampedCount = currentCount.coerceIn(0, safeTarget)
        val normalized = (clampedCount.toFloat() / safeTarget.toFloat()).coerceIn(0f, 1f)
        val stage = (normalized * 10f).toInt().coerceIn(0, 10)
        val theme = getTheme(themeId)
        val desc = theme.stageDescriptions.getOrElse(stage) { theme.shortSubtitle }
        return StageProgress(
            currentCount = clampedCount,
            targetCount = safeTarget,
            normalizedProgress = normalized,
            stage = stage,
            stageDescription = desc
        )
    }
}
