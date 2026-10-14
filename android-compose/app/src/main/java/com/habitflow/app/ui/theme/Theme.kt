package com.habitflow.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF818CF8),       // brand-400
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4338CA), // brand-700
    secondary = Color(0xFFA855F7),     // purple-500
    tertiary = Color(0xFF10B981),      // emerald-500
    background = Color(0xFF020617),    // slate-950
    surface = Color(0xFF0F172A),       // slate-900
    surfaceVariant = Color(0xFF1E293B),// slate-800
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFF94A3B8), // slate-400
    outline = Color(0xFF334155),       // slate-700
    error = Color(0xFFF43F5E)          // rose-500
)

@Composable
fun HabitFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(),
        content = content
    )
}

// Accent color palette matching the HTML version
object AccentColors {
    val Indigo = Color(0xFF6366F1)
    val Emerald = Color(0xFF10B981)
    val Rose = Color(0xFFF43F5E)
    val Amber = Color(0xFFF59E0B)
    val Cyan = Color(0xFF06B6D4)
    val Purple = Color(0xFFA855F7)
}
