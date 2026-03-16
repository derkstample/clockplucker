package com.example.clockplucker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.clockplucker.data.Player
import com.example.clockplucker.data.local.AppDatabase
import com.example.clockplucker.data.local.ScriptRepository
import com.example.clockplucker.ui.CharacterConfirmationScreen
import com.example.clockplucker.ui.CharacterSelectScreen
import com.example.clockplucker.ui.GrimRevealScreen
import com.example.clockplucker.ui.OptionsScreen
import com.example.clockplucker.ui.PlayerListScreen
import com.example.clockplucker.ui.PlayerReadyScreen
import com.example.clockplucker.ui.ScriptScreen
import com.example.clockplucker.ui.theme.ClockPluckerTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ScriptRepository(database.scriptDao())

        val viewModel: MainViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(repository) as T
                }
            }
        }

        setContent {
            ClockPluckerApp(viewModel)
        }
    }
}

sealed class Screen(val route: String) {
    object ScriptScreen : Screen("script_screen")
    object OptionsScreen : Screen("options_screen")
    object PlayerListScreen : Screen("player_list_screen")
    object PlayerReadyScreen : Screen("player_ready_screen")
    object CharacterSelectScreen : Screen("character_select_screen")
    object CharacterConfirmationScreen : Screen("character_confirmation_screen")
    object GrimRevealScreen : Screen("grim_reveal_screen")
}

@Composable
fun ClockPluckerApp(viewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.ScriptScreen.route) {
        composable(Screen.ScriptScreen.route) {
            ScriptScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.OptionsScreen.route) },
                viewModel = viewModel
            )
        }
        composable(Screen.OptionsScreen.route) {
            OptionsScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.PlayerListScreen.route) },
                viewModel = viewModel
            )
        }
        composable(Screen.PlayerListScreen.route) {
            PlayerListScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.PlayerReadyScreen.route) },
                viewModel = viewModel
            )
        }
        composable(Screen.PlayerReadyScreen.route) {
            PlayerReadyScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.CharacterSelectScreen.route) },
                viewModel = viewModel
            )
        }
        composable(Screen.CharacterSelectScreen.route) {
            CharacterSelectScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.CharacterConfirmationScreen.route) },
                viewModel = viewModel
            )
        }
        composable(Screen.CharacterConfirmationScreen.route) {
            CharacterConfirmationScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.GrimRevealScreen.route) },
                viewModel = viewModel
            )
        }
        composable(Screen.GrimRevealScreen.route) {
            GrimRevealScreen(
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.ScriptScreen.route) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun HelpButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Help",
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun NInputField(
    value: String,
    onValueChange: (String) -> Unit,
    focusRequester: FocusRequester,
    onFocus: () -> Unit
) {
    var textFieldValue by remember(value) {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(0, value.length)))
    }

    BasicTextField(
        value = textFieldValue,
        onValueChange = {
            if (it.text.isEmpty() || (it.text.toIntOrNull() != null && it.text.toInt() in 1..99)) {
                textFieldValue = it
                onValueChange(it.text)
            }
        },
        modifier = Modifier
            .width(24.dp)
            .focusRequester(focusRequester)
            .onFocusChanged {
                if (it.isFocused) {
                    textFieldValue = textFieldValue.copy(selection = TextRange(0, textFieldValue.text.length))
                    onFocus()
                } else {
                    if (textFieldValue.text.isEmpty()) {
                        onValueChange("1")
                    }
                }
            },
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface,
            textDecoration = TextDecoration.Underline
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

fun Modifier.verticalScrollbar(
    state: ScrollState,
    alpha: Float,
    width: Dp = 4.dp,
    color: Color = Color.Gray
): Modifier = drawWithContent {
    drawContent()
    if (alpha > 0f && state.maxValue > 0) {
        val viewPortHeight = size.height
        val contentHeight = state.maxValue + viewPortHeight
        val scrollbarHeight = (viewPortHeight / contentHeight) * viewPortHeight
        val scrollbarTop = (state.value.toFloat() / contentHeight) * viewPortHeight

        drawRoundRect(
            color = color,
            topLeft = Offset(size.width - width.toPx(), scrollbarTop),
            size = Size(width.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2),
            alpha = alpha
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScriptScreenPreview() {
    ClockPluckerTheme {
        ScriptScreen(
            onBack = {},
            onNext = {},
            viewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(ScriptRepository(object : com.example.clockplucker.data.local.ScriptDao {
                        override fun getAllScripts() = kotlinx.coroutines.flow.flowOf(emptyList<com.example.clockplucker.data.local.SavedScript>())
                        override suspend fun insertScript(script: com.example.clockplucker.data.local.SavedScript) {}
                        override suspend fun deleteScript(script: com.example.clockplucker.data.local.SavedScript) {}
                        override suspend fun updateScript(script: com.example.clockplucker.data.local.SavedScript) {}
                    })) as T
                }
            })
        )
    }
}
