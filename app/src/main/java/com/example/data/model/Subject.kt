package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class Subject(
    val displayName: String,
    val shortName: String,
    val accentColor: Long,
    val lightAccentColor: Long
) {
    PHYSICS("Physics", "PHY", 0xFF38BDF8, 0xFF0284C7),
    CHEMISTRY("Chemistry", "CHEM", 0xFF34D399, 0xFF059669),
    MATHEMATICS("Mathematics", "MATH", 0xFFA78BFA, 0xFF7C3AED);

    fun getColor(): Color = Color(accentColor)
    fun getSecondaryColor(): Color = Color(lightAccentColor)
}
