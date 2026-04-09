package clockplucker.ui.theme

//    Copyright 2026 Derek Rodriguez
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.

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
    primary = LightPrimary,
    secondary = LightSecondary,
    tertiary = LightTertiary
)

private val LightColorScheme = lightColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary
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

private val FabledColorScheme = darkColorScheme(
    primary = FabledPrimary,
    onPrimary = FabledOnPrimary,
    primaryContainer = FabledPrimaryContainer,
    onPrimaryContainer = FabledOnPrimaryContainer,
    surfaceVariant = FabledPrimaryContainer.copy(alpha = 0.2f),
    onSurfaceVariant = FabledPrimary
)

private val LoricColorScheme = darkColorScheme(
    primary = LoricPrimary,
    onPrimary = LoricOnPrimary,
    primaryContainer = LoricPrimaryContainer,
    onPrimaryContainer = LoricOnPrimaryContainer,
    surfaceVariant = LoricPrimaryContainer.copy(alpha = 0.2f),
    onSurfaceVariant = LoricPrimary
)

private val DisabledColorScheme = darkColorScheme(
    primary = DisabledPrimary,
    onPrimary = DisabledOnPrimary,
    primaryContainer = DisabledPrimaryContainer,
    onPrimaryContainer = DisabledOnPrimaryContainer,
    surfaceVariant = DisabledPrimaryContainer.copy(alpha = 0.2f),
    onSurfaceVariant = DisabledPrimary
)

@Composable
fun GoodTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GoodColorScheme,
        typography = AppTypography,
        content = content
    )
}

@Composable
fun EvilTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EvilColorScheme,
        typography = AppTypography,
        content = content
    )
}

@Composable
fun FabledTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FabledColorScheme,
        typography = AppTypography,
        content = content
    )
}

@Composable
fun LoricTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LoricColorScheme,
        typography = AppTypography,
        content = content
    )
}

@Composable
fun DisabledTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DisabledColorScheme,
        typography = AppTypography,
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
        typography = AppTypography,
        content = content
    )
}
