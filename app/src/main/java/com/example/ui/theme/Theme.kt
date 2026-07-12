package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NoxColorScheme = darkColorScheme(
    primary = EmeraldNeon,
    secondary = EmeraldNeon,
    tertiary = AmberNeon,
    background = Black,
    surface = SurfaceDark,
    error = RedNeon,
    onPrimary = Black,
    onSecondary = Black,
    onBackground = White,
    onSurface = White,
    onError = White
)

@Composable
fun NoxTabungkuTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NoxColorScheme,
        typography = Typography,
        content = content
    )
}
