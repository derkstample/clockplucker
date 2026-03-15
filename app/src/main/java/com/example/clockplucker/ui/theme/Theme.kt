package com.example.clockplucker.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

private val GoodColorScheme = darkColorScheme(
    primary = GoodPrimary,
    onPrimary = GoodOnPrimary,
    primaryContainer = GoodPrimaryContainer,
    onPrimaryContainer = GoodOnPrimaryContainer,
    surfaceVariant = GoodPrimaryContainer.copy(alpha = 0.2f),
    onSurfaceVariant = GoodPrimary
)

private val EvilColorScheme = darkColorScheme(
    primary = EvilPrimary,
    onPrimary = EvilOnPrimary,
    primaryContainer = EvilPrimaryContainer,
    onPrimaryContainer = EvilOnPrimaryContainer,
    surfaceVariant = EvilPrimaryContainer.copy(alpha = 0.2f),
    onSurfaceVariant = EvilPrimary
)

@Composable
fun GoodTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GoodColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun EvilTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EvilColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun ClockPluckerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
