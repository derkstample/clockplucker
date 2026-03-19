package com.example.clockplucker.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.SelectedPriorities
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType
import com.example.clockplucker.data.Player
import com.example.clockplucker.lazyVerticalScrollbar
import com.example.clockplucker.ui.theme.EvilOnPrimaryContainer
import com.example.clockplucker.ui.theme.EvilPrimary
import com.example.clockplucker.ui.theme.EvilPrimaryContainer
import com.example.clockplucker.ui.theme.GoodOnPrimaryContainer
import com.example.clockplucker.ui.theme.GoodPrimary
import com.example.clockplucker.ui.theme.GoodPrimaryContainer
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
    var activeMenuIndex by remember { mutableStateOf<Int?>(null) }

    val reorderableState = rememberReorderableLazyListState(listState) { from, to ->
        viewModel.movePlayer(from.index, to.index)
    }

    val isNextEnabled by remember {
        derivedStateOf {
            viewModel.players.all { it.name.isNotBlank() }
        }
    }

    val scrollbarAlpha by remember { 
        derivedStateOf { 
            if (listState.isScrollInProgress) 1f else 0f
        }
    }
    val animatedAlpha by animateFloatAsState(
        targetValue = scrollbarAlpha,
        animationSpec = tween(durationMillis = 300),
        label = "ScrollbarAlpha"
    )

    // remember lambdas for performance
    val onPlayerChange = remember(viewModel) {
        { i: Int, p: Player -> viewModel.updatePlayer(i, p) }
    }

    val onDeletePlayer = remember(viewModel) {
        { i: Int ->
            if (viewModel.players.size <= 5) {
                viewModel.updatePlayer(i, Player())
            } else {
                viewModel.removePlayer(i)
            }
        }
    }

    val onOpenMenu = remember(viewModel) {
        { i: Int -> activeMenuIndex = i }
    }

    val onDismissMenu = remember(viewModel) {
        { activeMenuIndex = null }
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
                        PlayerInputRow(
                            index = index,
                            player = player,
                            selectedPriority = viewModel.selectedPriority,
                            onPlayerChange = onPlayerChange,
                            onDeletePlayer = onDeletePlayer,
                            isMenuExpanded = activeMenuIndex == index,
                            onOpenMenu = onOpenMenu,
                            onDismissMenu = onDismissMenu,
                            handleModifier = Modifier.longPressDraggableHandle(
                                onDragStarted = {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            ),
                            modifier = Modifier
                                .animateItem()
                                .zIndex(if (isDragging) 1f else 0f)
                                .graphicsLayer {
                                    if (isDragging) {
                                        scaleX = 1.02f
                                        scaleY = 1.02f
                                        shadowElevation = 8.dp.toPx()
                                        clip = true
                                    }
                                }
                                .padding(horizontal = 8.dp)
                        )
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

data class PlayerRowTheme(
    val surfaceColor: Color,
    val containerColor: Color,
    val contentColor: Color,
    val labelColor: Color
)

@Composable
fun PlayerCountHeader (
    text: String,
    modifier: Modifier = Modifier,
    count: Int,
    onCountChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val range = remember { (5..20).toList() }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
    ) {
        if (text.isNotEmpty()) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
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
            Text(text = "COUNT: $count", style = MaterialTheme.typography.labelMedium)
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                range.forEach { n ->
                    DropdownMenuItem(
                        text = { Text(n.toString(), style = MaterialTheme.typography.labelMedium) },
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
    isMenuExpanded: Boolean,
    onOpenMenu: (Int) -> Unit,
    onDismissMenu: () -> Unit,
    modifier: Modifier = Modifier,
    handleModifier: Modifier = Modifier
) {
    val themePrimaryContainer = MaterialTheme.colorScheme.primaryContainer
    val themeOnPrimaryContainer = MaterialTheme.colorScheme.onPrimaryContainer
    val themePrimary = MaterialTheme.colorScheme.primary
    val focusManager = LocalFocusManager.current

    val rowTheme = remember(player.alignmentPriority, player.typePriority, selectedPriority) {
        val isGood = (player.alignmentPriority == CharAlignment.GOOD && selectedPriority == SelectedPriorities.ALIGNMENT) ||
                ((player.typePriority == CharType.TOWNSFOLK || player.typePriority == CharType.OUTSIDER) && selectedPriority == SelectedPriorities.TYPE)

        val isEvil = (player.alignmentPriority == CharAlignment.EVIL && selectedPriority == SelectedPriorities.ALIGNMENT) ||
                ((player.typePriority == CharType.MINION || player.typePriority == CharType.DEMON) && selectedPriority == SelectedPriorities.TYPE)

        PlayerRowTheme(
            surfaceColor = when {
                isGood -> GoodPrimaryContainer.copy(alpha = 0.15f)
                isEvil -> EvilPrimaryContainer.copy(alpha = 0.15f)
                else -> Color.Transparent
            },
            containerColor = when {
                isGood -> GoodPrimaryContainer.copy(alpha = 0.4f)
                isEvil -> EvilPrimaryContainer.copy(alpha = 0.4f)
                else -> themePrimaryContainer.copy(alpha = 0.5f)
            },
            contentColor = when {
                isGood -> GoodOnPrimaryContainer
                isEvil -> EvilOnPrimaryContainer
                else -> themeOnPrimaryContainer
            },
            labelColor = when {
                isGood -> GoodPrimary
                isEvil -> EvilPrimary
                else -> themePrimary
            }
        )
    }
    val animatedSurfaceColor by animateColorAsState(
        targetValue = rowTheme.surfaceColor,
        label = "rowSurfaceColor",
        animationSpec = tween(durationMillis = 200)
    )

    val colors = TextFieldDefaults.colors(
        focusedTextColor = rowTheme.contentColor,
        unfocusedTextColor = rowTheme.contentColor,
        focusedContainerColor = rowTheme.containerColor,
        unfocusedContainerColor = rowTheme.containerColor,
        disabledContainerColor = rowTheme.containerColor,
        cursorColor = rowTheme.labelColor,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
    )

    val textFieldColors = remember(rowTheme) { colors }

    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .drawBehind { drawRect(animatedSurfaceColor) }
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
                        color = rowTheme.labelColor,
                        modifier = Modifier
                            .padding(start = 36.dp, bottom = 2.dp)
                            .weight(1f)
                    )
                    if (selectedPriority == SelectedPriorities.ALIGNMENT || selectedPriority == SelectedPriorities.TYPE) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedPriority == SelectedPriorities.ALIGNMENT) "Alignment" else "Type",
                            style = MaterialTheme.typography.bodySmall,
                            color = rowTheme.labelColor,
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
                        .height(56.dp),
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
                        placeholder = { Text(text = "Enter Name", style = MaterialTheme.typography.bodyMedium, color = rowTheme.contentColor.copy(alpha = 0.7f)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { defaultKeyboardAction(ImeAction.Done) },
                            onNext = { defaultKeyboardAction(ImeAction.Next) }
                        ),
                        shape = if (showPriority) RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp) else MaterialTheme.shapes.small,
                        colors = textFieldColors,
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    if (showPriority) {
                        PriorityDropdown(
                            selected = if (selectedPriority == SelectedPriorities.ALIGNMENT)
                                (player.alignmentPriority?.name ?: "EITHER")
                            else
                                (player.typePriority?.name ?: "ANY"),
                            isExpanded = isMenuExpanded,
                            openIndex = index,
                            onOpen = {
                                focusManager.clearFocus()
                                onOpenMenu(index)
                            },
                            onDismiss = onDismissMenu,
                            onSelect = { option ->
                                if (selectedPriority == SelectedPriorities.ALIGNMENT) {
                                    val newAlignment = if (option == "EITHER") null else CharAlignment.valueOf(option)
                                    onPlayerChange(index, player.copy(alignmentPriority = newAlignment, typePriority = null))
                                } else {
                                    val newType = if (option == "ANY") null else CharType.valueOf(option)
                                    onPlayerChange(index, player.copy(typePriority = newType, alignmentPriority = null))
                                }
                            },
                            options = if (selectedPriority == SelectedPriorities.ALIGNMENT)
                                listOf("EITHER", "GOOD", "EVIL")
                            else
                                listOf("ANY", "TOWNSFOLK", "OUTSIDER", "MINION", "DEMON"),
                            containerColor = rowTheme.containerColor,
                            contentColor = rowTheme.contentColor,
                            modifier = Modifier
                                .width(130.dp)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
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
        HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
            )
    }
}

@Composable
fun PriorityDropdown(
    selected: String,
    isExpanded: Boolean,
    openIndex: Int,
    onOpen: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit,
    options: List<String>,
    containerColor: Color,
    contentColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.small
) {
    Box(modifier = modifier) {
        Surface(
            color = containerColor,
            shape = shape,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clickable { onOpen(openIndex) }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                AutoResizingText(
                    text = selected,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = contentColor
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = onDismiss
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.labelMedium) },
                    onClick = {
                        onSelect(option)
                        onDismiss()
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
    var fontSizeValue by remember(text) { mutableFloatStateOf(style.fontSize.value) }
    var readyToDraw by remember(text) { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            if (readyToDraw) drawContent() // Only draw once size is calculated
        },
        style = style.copy(fontSize = fontSizeValue.sp),
        maxLines = 1,
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth && fontSizeValue > 8f) {
                fontSizeValue *= 0.9f // Reduce size
            } else {
                readyToDraw = true // Lock in size
            }
        }
    )
}