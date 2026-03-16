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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Scaffold
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
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType
import com.example.clockplucker.data.Player
import com.example.clockplucker.ui.theme.EvilTheme
import com.example.clockplucker.ui.theme.GoodTheme
import java.util.Collections

@Composable
fun PlayerListScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                onBack = onBack,
                onNext = onNext,
                progress = 3
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

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
    
    var rowHeightPx by remember { mutableStateOf(0f) }

    val currentIndex by rememberUpdatedState(index)
    val currentDraggedIndex by rememberUpdatedState(draggedIndex)
    val currentPlayers by rememberUpdatedState(players)
    val currentRowHeight by rememberUpdatedState(rowHeightPx)

    Column(
        modifier = modifier
            .onGloballyPositioned { rowHeightPx = it.size.height.toFloat() }
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
                    contentDescription = "Drag Handle"
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                value = player.name,
                onValueChange = { onPlayerChange(index, player.copy(name = it)) },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Enter Name") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .clickable { onDeletePlayer(index) },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
