package com.example.clockplucker.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.SelectedPriorities
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType
import com.example.clockplucker.data.Player
import com.example.clockplucker.lazyVerticalScrollbar
import com.example.clockplucker.ui.theme.EvilTheme
import com.example.clockplucker.ui.theme.GoodTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun PlayerListScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val listState = rememberLazyListState()
    val haptic = LocalHapticFeedback.current

    val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
        viewModel.movePlayer(from.index, to.index)
    }

    val isNextEnabled by remember {
        derivedStateOf {
            viewModel.players.all { it.name.isNotBlank() }
        }
    }

    var scrollbarAlpha by remember { mutableFloatStateOf(0f) }
    val animatedAlpha by animateFloatAsState(
        targetValue = scrollbarAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "ScrollbarAlpha"
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collectLatest {
                scrollbarAlpha = 1f
                delay(1000)
                scrollbarAlpha = 0f
            }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                onBack = onBack,
                onNext = onNext,
                progress = 3,
                nextEnabled = isNextEnabled
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            PlayerCountHeader(
                text = "PLAYER LIST",
                modifier = Modifier.padding(horizontal = 16.dp),
                count = viewModel.players.size,
                onCountChange = {
                    val currentPlayers = viewModel.players.toList()
                    val newPlayers = List(it) { i ->
                        if (i < currentPlayers.size) currentPlayers[i] else Player()
                    }
                    viewModel.updatePlayers(newPlayers)
                }
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .lazyVerticalScrollbar(
                        state = listState,
                        alpha = animatedAlpha,
                        rightPadding = 4.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
            ) {
                itemsIndexed(
                    items = viewModel.players,
                    key = { _, player -> player.id }
                ) { index, player ->
                    ReorderableItem(reorderableState, key = player.id) { isDragging ->
                        Column(
                            modifier = Modifier
                                .animateItem()
                                .zIndex(if (isDragging) 1f else 0f)
                        ) {
                            PlayerInputRow(
                                index = index,
                                player = player,
                                selectedPriority = viewModel.selectedPriority,
                                onPlayerChange = { i, p -> viewModel.updatePlayer(i, p) },
                                onDeletePlayer = { i ->
                                    if (viewModel.players.size <= 5) {
                                        viewModel.updatePlayer(i, Player())
                                    } else {
                                        viewModel.removePlayer(i)
                                    }
                                },
                                handleModifier = Modifier.longPressDraggableHandle(
                                    onDragStarted = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                ),
                                modifier = Modifier
                                    .graphicsLayer {
                                        if (isDragging) {
                                            scaleX = 1.02f
                                            scaleY = 1.02f
                                            shadowElevation = 8.dp.toPx()
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 8.dp),
                                isLast = index == viewModel.players.size - 1
                            )
                            if (index < viewModel.players.size - 1) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                                )
                            }
                        }
                    }
                }
                item(key = "add_player_button") {
                    if (viewModel.players.size < 20) {
                        Box(
                            modifier = Modifier
                                .animateItem()
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .clickable { viewModel.addPlayer() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Player"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerCountHeader (
    text: String,
    modifier: Modifier = Modifier,
    count: Int,
    onCountChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
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
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Text(text = "COUNT: $count", style = MaterialTheme.typography.labelLarge)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                (5..20).forEach { n ->
                    DropdownMenuItem(
                        text = { Text(n.toString(), style = MaterialTheme.typography.labelLarge) },
                        onClick = {
                            onCountChange(n)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlayerInputRow(
    index: Int,
    player: Player,
    selectedPriority: SelectedPriorities,
    onPlayerChange: (Int, Player) -> Unit,
    onDeletePlayer: (Int) -> Unit,
    modifier: Modifier = Modifier,
    handleModifier: Modifier = Modifier,
    isLast: Boolean = false,
) {
    val isGood = (player.alignmentPriority == CharAlignment.GOOD && selectedPriority == SelectedPriorities.ALIGNMENT) ||
            ((player.typePriority == CharType.TOWNSFOLK || player.typePriority == CharType.OUTSIDER) && selectedPriority == SelectedPriorities.TYPE)

    val isEvil = (player.alignmentPriority == CharAlignment.EVIL && selectedPriority == SelectedPriorities.ALIGNMENT) ||
            ((player.typePriority == CharType.MINION || player.typePriority == CharType.DEMON) && selectedPriority == SelectedPriorities.TYPE)

    val content = @Composable {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "Player ${index + 1}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(start = 36.dp, bottom = 2.dp)
                            .weight(1f)
                    )
                    if (selectedPriority == SelectedPriorities.ALIGNMENT || selectedPriority == SelectedPriorities.TYPE) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPriority == SelectedPriorities.ALIGNMENT) "Alignment" else "Type",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .width(130.dp)
                                .padding(bottom = 2.dp),
                            textAlign = TextAlign.Start
                        )
                    }
                    Spacer(modifier = Modifier.width(32.dp))
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 28.dp, height = 56.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .then(handleModifier),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.List,
                            contentDescription = "Drag Handle"
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    val showPriority = selectedPriority == SelectedPriorities.ALIGNMENT || selectedPriority == SelectedPriorities.TYPE

                    TextField(
                        value = player.name,
                        onValueChange = { onPlayerChange(index, player.copy(name = it)) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        placeholder = { Text(text = "Enter Name", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer) },
                        singleLine = true,
                        keyboardOptions = if (isLast) KeyboardOptions(imeAction = ImeAction.Done) else KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                defaultKeyboardAction(ImeAction.Done)
                            },
                            onNext = {
                                defaultKeyboardAction(ImeAction.Next)
                            }
                        ),
                        shape = if (showPriority) RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp) else MaterialTheme.shapes.small,
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    if (showPriority) {
                        if (selectedPriority == SelectedPriorities.ALIGNMENT) {
                            PriorityDropdown(
                                selected = player.alignmentPriority?.name ?: "EITHER",
                                options = listOf("EITHER", "GOOD", "EVIL"),
                                onSelect = { option ->
                                    val newAlignment = if (option == "EITHER") null else CharAlignment.valueOf(option)
                                    onPlayerChange(index, player.copy(alignmentPriority = newAlignment, typePriority = null))
                                },
                                modifier = Modifier
                                    .width(130.dp)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                            )
                        } else {
                            PriorityDropdown(
                                selected = player.typePriority?.name ?: "ANY",
                                options = listOf("ANY", "TOWNSFOLK", "OUTSIDER", "MINION", "DEMON"),
                                onSelect = { option ->
                                    val newType = if (option == "ANY") null else CharType.valueOf(option)
                                    onPlayerChange(index, player.copy(typePriority = newType, alignmentPriority = null))
                                },
                                modifier = Modifier
                                    .width(130.dp)
                                    .fillMaxHeight(),
                                shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .clickable { onDeletePlayer(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete"
                        )
                    }
                }
            }
        }
    }

    when {
        isGood -> GoodTheme { content() }
        isEvil -> EvilTheme { content() }
        else -> content()
    }
}

@Composable
fun PriorityDropdown(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.75f),
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable { expanded = true }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AutoResizingText(
                    text = selected,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.labelLarge) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun AutoResizingText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    var fontSizeValue by remember(text) { mutableStateOf(style.fontSize) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent()
        },
        style = style.copy(fontSize = fontSizeValue),
        maxLines = 1,
        softWrap = false,
        textAlign = textAlign,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                fontSizeValue *= 0.9f
            } else {
                readyToDraw = true
            }
        }
    )
}
