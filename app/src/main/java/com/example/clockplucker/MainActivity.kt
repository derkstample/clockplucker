package com.example.clockplucker

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clockplucker.ui.theme.ClockPluckerTheme
import com.example.clockplucker.ui.theme.EvilTheme
import com.example.clockplucker.ui.theme.GoodTheme
import kotlinx.coroutines.delay
import java.util.Collections

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClockPluckerTheme {
                MainScreen()
            }
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

@Composable
fun MainScreen() {
    var loadedScript by remember { mutableStateOf<Script?>(null) }
    var isScriptSelectExpanded by remember { mutableStateOf(true) }
    var players by remember { mutableStateOf(List(5) { Player() }) }
    val playerCount by remember { derivedStateOf { players.size } }
    var isPlayerListExpanded by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var isGardeningOptionsExpanded by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableIntStateOf(1) }
    var selectedPriority by remember { mutableIntStateOf(1) }
    var gardeningToggle by remember { mutableStateOf(false) }
    var helpDialogText by remember { mutableStateOf<String?>(null) }

    var alignmentN by remember { mutableStateOf("1") }
    var typeN by remember { mutableStateOf("1") }
    val alignmentFocusRequester = remember { FocusRequester() }
    val typeFocusRequester = remember { FocusRequester() }

    var draggedIndex by remember { mutableStateOf<Int?>(null) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val content =
                context.contentResolver.openInputStream(it)?.bufferedReader()?.use { reader ->
                    reader.readText()
                }
            content?.let { json ->
                val script = ScriptLoader().parseScript(json)
                loadedScript = script
                if (script.characters.isNotEmpty()) {
                    isScriptSelectExpanded = false
                    isGardeningOptionsExpanded = true
                }
            }
        }
    }

    if (helpDialogText != null) {
        AlertDialog(
            onDismissRequest = { helpDialogText = null },
            confirmButton = {
                TextButton(onClick = { helpDialogText = null }) {
                    Text("OK")
                }
            },
            text = { Text(helpDialogText!!) }
        )
    }

    val scrollState = rememberScrollState()
    val isDragging = draggedIndex != null

    var scrollbarVisible by remember { mutableStateOf(false) }
    val scrollbarAlpha by animateFloatAsState(
        targetValue = if (scrollbarVisible) 0.5f else 0f,
        label = "scrollbar_alpha"
    )

    LaunchedEffect(
        scrollState.value,
        scrollState.maxValue
    ) {
        // Check if the content is actually larger than the viewport
        val isScrollable = scrollState.maxValue > 0

        if (isScrollable) {
            scrollbarVisible = true
            delay(500)
            scrollbarVisible = false
        } else {
            scrollbarVisible = false
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .imePadding()
                .verticalScrollbar(scrollState, scrollbarAlpha)
                .verticalScroll(
                    state = scrollState,
                    enabled = !isDragging
                )
                .animateContentSize(spring(stiffness = Spring.StiffnessMedium)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Row
            HeaderRow()

            // Select Script container
            SelectScriptContainer(
                launcher = launcher,
                isExpanded = isScriptSelectExpanded,
                onToggle = { isScriptSelectExpanded = !isScriptSelectExpanded }
            )
            
            // Script Info Label
            ScriptInfoLabel(
                isVisible = isScriptSelectExpanded,
                loadedScript = loadedScript
            )

            // Gardening Options Button
            GardeningOptionsButton(
                isExpanded = isGardeningOptionsExpanded,
                onToggle = { isGardeningOptionsExpanded = !isGardeningOptionsExpanded }
            )

            // Animated Gardening Options
            AnimatedGardeningOptionsContainer(
                isVisible = isGardeningOptionsExpanded,
                selectedMode = selectedMode,
                onModeChange = { selectedMode = it },
                alignmentN = alignmentN,
                onAlignmentNChange = { alignmentN = it },
                typeN = typeN,
                onTypeNChange = { typeN = it },
                selectedPriority = selectedPriority,
                onPriorityChange = { selectedPriority = it },
                gardeningToggle = gardeningToggle,
                onGardeningToggleChange = { gardeningToggle = !gardeningToggle },
                onHelpDialogTextChange = { helpDialogText = it },
                alignmentFocusRequester = alignmentFocusRequester,
                typeFocusRequester = typeFocusRequester
            )

            // Player Count and Toggle Container
            PlayerCountContainer(
                isVisible = dropdownExpanded,
                onVisibilityChange = { dropdownExpanded = it },
                onGardeningToggleChange = { isGardeningOptionsExpanded = it },
                isPlayerListExpanded = isPlayerListExpanded,
                onPlayerListToggleChange = { isPlayerListExpanded = it },
                playerCount = playerCount,
                players = players,
                onPlayersChange = { players = it }
            )

            // Animated Player Name List
            PlayerNameInputList(
                players = players,
                onPlayerChange = { index, player ->
                    val newPlayers = players.toMutableList()
                    newPlayers[index] = player
                    players = newPlayers
                },
                onDeletePlayer = { index ->
                    if (players.size > 5) {
                        val newPlayers = players.toMutableList()
                        newPlayers.removeAt(index)
                        players = newPlayers
                    } else {
                        val newPlayers = players.toMutableList()
                        newPlayers[index] = Player()
                        players = newPlayers
                    }
                },
                onPlayersChange = { players = it },
                selectedPriority = selectedPriority,
                isPlayerListExpanded = isPlayerListExpanded,
                draggedIndex = draggedIndex,
                onDraggedIndexChange = { draggedIndex = it }
            )
        }
    }
}

@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.clockplucker),
            contentDescription = "Clock Plucker Logo",
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = "Clock Plucker",
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Composable
fun SelectScriptContainer(
    launcher: ActivityResultLauncher<String>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Zone: Select script
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { launcher.launch("application/json") }
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Select Script",
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            // Vertical Divider
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
            )

            // Right Zone: Show/Hide Toggle
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onToggle() }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val scriptRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(scriptRotation)
                )
            }
        }
    }
}

@Composable
fun ScriptInfoLabel(
    isVisible: Boolean,
    loadedScript: Script?
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        if (loadedScript == null || loadedScript.characters.isEmpty()) {
            Text(
                text = "No script selected.",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = loadedScript.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "by ${loadedScript.author}",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
fun GardeningOptionsButton(
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Button(
        onClick = { onToggle() },
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gardening Options",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            val gardeningRotation by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f)
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.rotate(gardeningRotation)
            )
        }
    }
}

@Composable
fun AnimatedGardeningOptionsContainer(
    isVisible: Boolean,

    selectedMode: Int,
    onModeChange: (Int) -> Unit,

    alignmentN: String,
    onAlignmentNChange: (String) -> Unit,

    typeN: String,
    onTypeNChange: (String) -> Unit,

    selectedPriority: Int,
    onPriorityChange: (Int) -> Unit,

    gardeningToggle: Boolean,
    onGardeningToggleChange: (Boolean) -> Unit,

    onHelpDialogTextChange: (String) -> Unit,

    alignmentFocusRequester: FocusRequester,
    typeFocusRequester: FocusRequester
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = expandVertically() + fadeIn(),
        exit = shrinkVertically() + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            SectionHeader("MODE")

            val modes = listOf(
                "No Restrictions" to "Players can select any number of preferred characters.",
                "n Of Each Alignment" to "Players can select up to n character(s) of each alignment (Good / Evil).",
                "n Of Each Type" to "Players can select up to n character(s) of each type (Townsfolk / Outsider / Minion / Demon)."
            )

            modes.forEachIndexed { index, pair ->
                val i = index + 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onModeChange(i)
                                if (i == 2) alignmentFocusRequester.requestFocus()
                                if (i == 3) typeFocusRequester.requestFocus()
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedMode == i,
                            onClick = {
                                onModeChange(i)
                                if (i == 2) alignmentFocusRequester.requestFocus()
                                if (i == 3) typeFocusRequester.requestFocus()
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))

                        when (i) {
                            2 -> {
                                NInputField(
                                    value = alignmentN,
                                    onValueChange = onAlignmentNChange,
                                    focusRequester = alignmentFocusRequester,
                                    onFocus = { onModeChange(2) }
                                )
                                Text(text = " Of Each Alignment")
                            }

                            3 -> {
                                NInputField(
                                    value = typeN,
                                    onValueChange = onTypeNChange,
                                    focusRequester = typeFocusRequester,
                                    onFocus = { onModeChange(3) }
                                )
                                Text(text = " Of Each Type")
                            }

                            else -> {
                                Text(text = pair.first)
                            }
                        }
                    }

                    val explanation = when (i) {
                        2 -> pair.second.replace(
                            " n ",
                            " ${if (alignmentN.isEmpty()) "n" else alignmentN} "
                        )

                        3 -> pair.second.replace(
                            " n ",
                            " ${if (typeN.isEmpty()) "n" else typeN} "
                        )

                        else -> pair.second
                    }
                    HelpButton(onClick = { onHelpDialogTextChange(explanation) })
                }
            }

            SectionHeader("STORYTELLER PRIORITIES")

            val priorities = listOf(
                "No Storyteller Priorities" to "The alignment and character type of each player is not influenced by the storyteller.",
                "Prioritize Alignments" to "The storyteller chooses the alignments of one or more players to prioritize (Good / Evil).",
                "Prioritize Types" to "The storyteller chooses the types of one or more players to prioritize (Townsfolk / Outsider / Minion / Demon)."
            )

            priorities.forEachIndexed { index, pair ->
                val i = index + 1
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onPriorityChange(i) }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = selectedPriority == i,
                            onClick = { onPriorityChange(i) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = pair.first)
                    }
                    HelpButton(onClick = { onHelpDialogTextChange(pair.second) })
                }
            }

            SectionHeader("") // Divider with no label

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onGardeningToggleChange(!gardeningToggle) }
                        .padding(vertical = 4.dp)
                ) {
                    Switch(
                        checked = gardeningToggle,
                        onCheckedChange = onGardeningToggleChange
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Enable Player Priorities")
                }
                HelpButton(onClick = {
                    onHelpDialogTextChange("Allows players to prioritize their preferred characters. The gardening algorithm will weigh each assignment accordingly.")
                })
            }
        }
    }
}

@Composable
fun PlayerCountContainer(
    isVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit,
    onGardeningToggleChange: (Boolean) -> Unit,

    isPlayerListExpanded: Boolean,
    onPlayerListToggleChange: (Boolean) -> Unit,

    playerCount: Int,
    players: List<Player>,
    onPlayersChange: (List<Player>) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(56.dp),
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Zone: Player Count
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable { onVisibilityChange(true) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Player Count: $playerCount",
                    style = MaterialTheme.typography.bodyLarge
                )

                DropdownMenu(
                    expanded = isVisible,
                    onDismissRequest = { onVisibilityChange(false) }
                ) {
                    (5..20).forEach { count ->
                        DropdownMenuItem(
                            text = { Text(count.toString()) },
                            onClick = {
                                onPlayersChange(
                                    List(count) { i ->
                                    if (i < players.size) players[i] else Player()
                                })
                                onVisibilityChange(false)
                                onGardeningToggleChange(false)
                                onPlayerListToggleChange(true)
                            }
                        )
                    }
                }
            }

            // Vertical Divider
            Spacer(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
            )

            // Right Zone: Show/Hide Toggle
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onPlayerListToggleChange(!isPlayerListExpanded) }
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val rotation by animateFloatAsState(targetValue = if (isPlayerListExpanded) 180f else 0f)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation)
                )
            }
        }
    }
}

@Composable
fun PlayerNameInputList(
    players: List<Player>,
    onPlayerChange: (Int, Player) -> Unit,
    onDeletePlayer: (Int) -> Unit,
    onPlayersChange: (List<Player>) -> Unit,

    selectedPriority: Int,
    isPlayerListExpanded: Boolean = false,
    draggedIndex: Int?,
    onDraggedIndexChange: (Int?) -> Unit
) {
    var draggingOffset by remember { mutableStateOf(0f) }

    players.forEachIndexed { index, player ->
        key(player.id) {
            AnimatedVisibility(
                visible = isPlayerListExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                PlayerInputRow(
                    index = index,
                    onPlayerChange = onPlayerChange,
                    onDeletePlayer = onDeletePlayer,
                    players = players,
                    onPlayersChange = onPlayersChange,
                    selectedPriority = selectedPriority,
                    draggedIndex = draggedIndex,
                    onDraggedIndexChange = onDraggedIndexChange,
                    draggingOffset = draggingOffset,
                    onDraggingOffsetChange = { draggingOffset = it },
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .graphicsLayer {
                            translationY = if (draggedIndex == index) draggingOffset else 0f
                            scaleX = if (draggedIndex == index) 1.05f else 1f
                            shadowElevation = if (draggedIndex == index) 8.dp.toPx() else 0f
                        }

                )
            }
        }
    }
}

@Composable
fun PlayerInputRow(
    index: Int,

    onPlayerChange: (Int, Player) -> Unit,
    onDeletePlayer: (Int) -> Unit,

    players: List<Player>,
    onPlayersChange: (List<Player>) -> Unit,

    selectedPriority: Int,

    draggedIndex: Int?,
    onDraggedIndexChange: (Int?) -> Unit,
    draggingOffset: Float,
    onDraggingOffsetChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val player = players[index]
    val alignment = when {
        selectedPriority == 2 -> player.alignmentPriority
        selectedPriority == 3 -> player.typePriority?.let {
            if (it == CharType.TOWNSFOLK || it == CharType.OUTSIDER) CharAlignment.GOOD else CharAlignment.EVIL
        }
        else -> null
    }

    var rowHeightPx by remember { mutableStateOf(0f) }

    val currentIndex by rememberUpdatedState(index)
    val currentDraggedIndex by rememberUpdatedState(draggedIndex)
    val currentPlayers by rememberUpdatedState(players)
    val currentRowHeight by rememberUpdatedState(rowHeightPx)


    val content = @Composable {
        Column(
            modifier = modifier
                .onGloballyPositioned { rowHeightPx = it.size.height.toFloat() } // this is to get the height of the row, to know when dragging should swap rows
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Player ${index + 1}",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 4.dp, start = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { onDraggedIndexChange(currentIndex) },
                                onDragEnd = {
                                    onDraggedIndexChange(null)
                                    onDraggingOffsetChange(0f)
                                },
                                onDragCancel = {
                                    onDraggedIndexChange(null)
                                    onDraggingOffsetChange(0f)
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()

                                    if (currentDraggedIndex == currentIndex) {
                                        val totalOffset = draggingOffset + dragAmount.y
                                        onDraggingOffsetChange(totalOffset)

                                        // SWAP DOWN
                                        if (totalOffset > currentRowHeight && currentIndex < currentPlayers.size - 1) {
                                            val newList = currentPlayers.toMutableList()
                                            Collections.swap(newList, currentIndex, currentIndex + 1)
                                            onPlayersChange(newList)

                                            // IMPORTANT: Offset the snap-back
                                            onDraggingOffsetChange(totalOffset - currentRowHeight)
                                            onDraggedIndexChange(currentIndex + 1)
                                        }
                                        // SWAP UP
                                        else if (totalOffset < -currentRowHeight && currentIndex > 0) {
                                            val newList = currentPlayers.toMutableList()
                                            Collections.swap(newList, currentIndex, currentIndex - 1)
                                            onPlayersChange(newList)

                                            onDraggingOffsetChange(totalOffset + currentRowHeight)
                                            onDraggedIndexChange(currentIndex - 1)
                                        }
                                    }
                                }
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Reorder Player",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                TextField(
                    value = player.name,
                    onValueChange = { onPlayerChange(index, player.copy(name = it)) },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Enter name") },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (index < players.size - 1) ImeAction.Next else ImeAction.Done
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                if (selectedPriority == 2) { // Prioritize Alignments
                    PriorityDropdown(
                        currentValue = player.alignmentPriority?.name ?: "EITHER",
                        options = listOf("EITHER", "GOOD", "EVIL"),
                        onValueChange = { selected ->
                            if (selected == "EITHER") {
                                onPlayerChange(index, player.copy(alignmentPriority = null))
                            } else {
                                onPlayerChange(
                                    index,
                                    player.copy(
                                        alignmentPriority = CharAlignment.valueOf(selected)
                                    )
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                } else if (selectedPriority == 3) { // Prioritize Types
                    PriorityDropdown(
                        currentValue = player.typePriority?.name ?: "ANY",
                        options = listOf("ANY", "TOWNSFOLK", "OUTSIDER", "MINION", "DEMON"),
                        onValueChange = { selected ->
                            if (selected == "ANY") {
                                onPlayerChange(index, player.copy(typePriority = null))
                            } else {
                                onPlayerChange(
                                    index,
                                    player.copy(typePriority = CharType.valueOf(selected))
                                )
                            }
                        }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onDeletePlayer(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete Player",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    when (alignment) {
        CharAlignment.GOOD -> GoodTheme { content() }
        CharAlignment.EVIL -> EvilTheme { content() }
        else -> content()
    }
}

@Composable
fun PriorityDropdown(
    currentValue: String,
    options: List<String>,
    onValueChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .width(140.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .clickable { expanded = true },
            color = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = currentValue,
                        style = MaterialTheme.typography.titleLarge,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// will use later
@Composable
fun CharacterRow(character: Character) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (character.icon != 0) {
            Image(
                painter = painterResource(id = character.icon),
                contentDescription = character.name,
                modifier = Modifier
                    .size(96.dp)
                    .aspectRatio(1f)
                    .padding(end = 8.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .padding(end = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("?")
            }
        }
        Column {
            Text(
                text = character.name,
                fontWeight = FontWeight.Bold
            )
            Text(text = character.ability)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ClockPluckerTheme {
        MainScreen()
    }
}
