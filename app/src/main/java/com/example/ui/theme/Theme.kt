package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = EmeraldLight,
    onPrimary = EmeraldDark,
    primaryContainer = EmeraldDark,
    onPrimaryContainer = EmeraldContainer,
    secondary = CyanLight,
    onSecondary = CyanDark,
    secondaryContainer = CyanDark,
    onSecondaryContainer = CyanContainer,
    background = MedicalDarkBg,
    onBackground = TextPrimaryLight,
    surface = MedicalDarkSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = MedicalDarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = MedicalDarkBorder,
    error = UrgencyEmergencyRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = EmeraldPrimary,
    onPrimary = Color.White,
    primaryContainer = EmeraldContainer,
    onPrimaryContainer = OnEmeraldContainer,
    secondary = CyanSecondary,
    onSecondary = Color.White,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = OnCyanContainer,
    background = MedicalSlateLight,
    onBackground = TextPrimaryDark,
    surface = MedicalSurfaceLight,
    onSurface = TextPrimaryDark,
    surfaceVariant = MedicalSurfaceVariantLight,
    onSurfaceVariant = TextSecondaryDark,
    outline = MedicalBorder,
    error = UrgencyEmergencyRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep medical brand colors consistent
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
