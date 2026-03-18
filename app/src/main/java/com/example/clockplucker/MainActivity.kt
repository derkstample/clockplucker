package com.example.clockplucker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch

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
            ClockPluckerTheme {
                ClockPluckerApp(viewModel)
            }
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
                onNext = { 
                    viewModel.updateLastAccessed()
                    navController.navigate(Screen.OptionsScreen.route) 
                },
                viewModel = viewModel
            )
        }
        composable(Screen.OptionsScreen.route) {
            OptionsScreen(
                onBack = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    } else {
                        navController.navigate(Screen.ScriptScreen.route)
                    }
                },
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
fun SectionHeader(text: String, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
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
            .padding(horizontal = 8.dp)
            .size(32.dp)
            .clip(shape = CircleShape)
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
fun NDropdown(
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int,
    max: Int
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Text(
            text = value.toString(),
            modifier = Modifier
                .clickable { expanded = true }
                .padding(horizontal = 4.dp),
            style = MaterialTheme.typography.labelLarge.copy(
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colorScheme.primary
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (min..max).forEach { n ->
                DropdownMenuItem(
                    text = { Text(n.toString(), style = MaterialTheme.typography.labelLarge) },
                    onClick = {
                        onValueChange(n)
                        expanded = false
                    }
                )
            }
        }
    }
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

fun Modifier.lazyVerticalScrollbar(
    state: LazyListState,
    alpha: Float,
    width: Dp = 4.dp,
    rightPadding: Dp = 0.dp,
    color: Color = Color.Gray
): Modifier = drawWithContent {
    drawContent()
    if (alpha > 0f) {
        val layoutInfo = state.layoutInfo
        val visibleItemsInfo = layoutInfo.visibleItemsInfo
        val totalItemsCount = layoutInfo.totalItemsCount
        
        if (visibleItemsInfo.isNotEmpty() && totalItemsCount > visibleItemsInfo.size) {
            val viewPortHeight = size.height
            
            // Assume consistent item height for consistent scrollbar height
            val firstItem = visibleItemsInfo.first()
            val itemHeight = firstItem.size.toFloat()
            val totalContentHeight = totalItemsCount * itemHeight
            
            if (totalContentHeight > viewPortHeight) {
                val scrollbarHeight = (viewPortHeight / totalContentHeight) * viewPortHeight
                
                val firstVisibleItemIndex = state.firstVisibleItemIndex
                val firstVisibleItemScrollOffset = state.firstVisibleItemScrollOffset
                
                val scrollOffset = (firstVisibleItemIndex * itemHeight + firstVisibleItemScrollOffset)
                val scrollbarTop = (scrollOffset / totalContentHeight) * viewPortHeight

                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width - width.toPx() - rightPadding.toPx(), scrollbarTop),
                    size = Size(width.toPx(), scrollbarHeight),
                    cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2),
                    alpha = alpha
                )
            }
        }
    }
}

@Composable
fun NavigationBar(
    progress: Int,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    onNext: (() -> Unit)? = null,
    nextEnabled: Boolean = true
) {
    val backTranslation = remember { Animatable(0f) }
    val nextTranslation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(56.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable {
                            scope.launch {
                                backTranslation.animateTo(-16f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                backTranslation.snapTo(0f)
                            }
                            onBack()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Back",
                        modifier = Modifier.offset(x = backTranslation.value.dp)
                    )
                }

                VerticalDivider(
                    modifier = Modifier
                        .height(48.dp)
                        .padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .weight(2f)
                    .fillMaxHeight(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isFilled = index < progress
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                color = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = CircleShape
                            )
                    )
                }
            }

            if (onNext != null) {
                VerticalDivider(
                    modifier = Modifier
                        .height(48.dp)
                        .padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(enabled = nextEnabled) {
                            scope.launch {
                                nextTranslation.animateTo(16f, animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy))
                                nextTranslation.snapTo(0f)
                            }
                            onNext()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next",
                        modifier = Modifier.offset(x = nextTranslation.value.dp),
                        tint = if (nextEnabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
                    )
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ScriptScreenPreview() {
    ClockPluckerTheme {
        ScriptScreen(
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

@Preview(showBackground = true)
@Composable
fun OptionsScreenPreview() {
    ClockPluckerTheme {
        OptionsScreen(
            onBack = {},
            onNext = {},
            viewModel = viewModel(factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return MainViewModel(ScriptRepository(object :
                        com.example.clockplucker.data.local.ScriptDao {
                        override fun getAllScripts() =
                            kotlinx.coroutines.flow.flowOf(emptyList<com.example.clockplucker.data.local.SavedScript>())

                        override suspend fun insertScript(script: com.example.clockplucker.data.local.SavedScript) {}
                        override suspend fun deleteScript(script: com.example.clockplucker.data.local.SavedScript) {}
                        override suspend fun updateScript(script: com.example.clockplucker.data.local.SavedScript) {}
                    })) as T
                }
            })
        )
    }
}
