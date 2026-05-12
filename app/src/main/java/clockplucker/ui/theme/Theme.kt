package clockplucker.ui.theme

//    Copyright 2026 Derek Rodriguez
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

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
