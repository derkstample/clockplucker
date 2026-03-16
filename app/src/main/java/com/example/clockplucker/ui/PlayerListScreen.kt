package com.example.clockplucker.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType
import com.example.clockplucker.data.Player
import com.example.clockplucker.ui.theme.EvilTheme
import com.example.clockplucker.ui.theme.GoodTheme
import java.util.Collections

@Composable
fun PlayerListScreen(
    onBack : () -> Unit,
    onNext : () -> Unit,
    viewModel: MainViewModel
) {

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
