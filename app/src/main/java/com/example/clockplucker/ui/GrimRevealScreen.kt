package com.example.clockplucker.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.SectionHeader
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.Character
import com.example.clockplucker.data.Player
import com.example.clockplucker.data.RoleSolver
import com.example.clockplucker.data.TypeCountLookup
import com.example.clockplucker.drawStableVerticalScrollbar
import com.example.clockplucker.ui.theme.EvilPrimary
import com.example.clockplucker.ui.theme.GoodPrimary
import java.util.Locale.getDefault

@Composable
fun GrimRevealScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val script = viewModel.loadedScript
    val characters = script?.characters ?: emptyList()
    val players = viewModel.players
    val lookup = remember { TypeCountLookup() }
    val containsPope = remember(script) { script?.containsPope ?: false }

    var assignments by remember { mutableStateOf<Map<Player, Pair<Character, Character?>>?>(null) }
    var revealed by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(script, players) {
        val solver = RoleSolver(
            players = players,
            availableChars = characters,
            baseCount = lookup.getBaseCounts(players.size),
            unselectableChance = viewModel.surpriseChance,
            selectedPriority = viewModel.selectedPriority,
            playerPriorityToggle = viewModel.playerPriorityToggle,
            containsPope = containsPope,
            autoSentinel = viewModel.autoSentinel
        )
        assignments = solver.optimizeAssignments()
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.bodyLarge
    val density = LocalDensity.current
    val maxNameWidth = remember(assignments) {
        val currentAssignments = assignments ?: return@remember 160.dp
        val maxWidthPx = currentAssignments.values.maxOf { (char, _) ->
            val words = char.name.split(Regex("\\s+"))
            words.maxOf { word ->
                textMeasurer.measure(
                    text = word,
                    style = labelStyle,
                    maxLines = 1
                ).size.width
            }
        }
        with(density) { (maxWidthPx.toDp() + 72.dp) }
    }

    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SectionHeader(
                text = "GRIMOIRE",
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (revealed && assignments != null) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .drawStableVerticalScrollbar(state = listState)
                ) {
                    itemsIndexed(
                        items = assignments!!.toList(),
                        key = { _, assignment -> assignment.first.id }
                    ) { index, assignment ->
                        GrimRow(
                            player = assignment.first,
                            pair = assignment.second,
                            index = index,
                            characterColumnWidth = maxNameWidth
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    val interactionSource = remember { MutableInteractionSource() }
                    val isPressed by interactionSource.collectIsPressedAsState()
                    val progress by animateFloatAsState(
                        targetValue = if (isPressed) 1f else 0f,
                        animationSpec = if (isPressed) {
                            tween(durationMillis = 1500, easing = LinearEasing)
                        } else {
                            snap()
                        },
                        label = "RevealProgress"
                    )

                    LaunchedEffect(progress) {
                        if (progress == 1f) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            revealed = true
                        }
                    }
                    GrimRevealButton(
                        interactionSource = interactionSource,
                        progress = progress,
                        primary = MaterialTheme.colorScheme.primary,
                        isWaiting = revealed && assignments == null
                    )
                }
            }
        }
    }
}

@Composable
fun GrimRevealButton(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    progress: Float,
    primary: Color,
    isWaiting: Boolean = false
){
    OutlinedButton(
        onClick = { },
        interactionSource = interactionSource,
        shape = ButtonDefaults.outlinedShape,
        modifier = Modifier
            .clip(ButtonDefaults.outlinedShape)
            .drawBehind {
                drawRect(
                    color = primary.copy(alpha = 0.15f),
                    size = Size(size.width * progress, size.height)
                )
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (isWaiting) "CALCULATING..." else "REVEAL GRIMOIRE",
                style = MaterialTheme.typography.labelLarge,
                color = primary
            )
            Text(
                text = if (isWaiting) "PLEASE WAIT" else "TAP AND HOLD",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = MaterialTheme.typography.labelSmall.fontSize
                ),
                color = primary.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun GrimRow(
    player: Player,
    pair: Pair<Character, Character?>,
    modifier: Modifier = Modifier,
    index: Int,
    characterColumnWidth: Dp
) {
    val character = remember(pair) { pair.first }
    val surprise = remember(pair) { pair.second }

    val displayCharacter = surprise ?: character

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = {}), //todo: dropdown to reveal the list of this player's selected characters
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Player ${index + 1}",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 2.dp)
                        .weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Character",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(bottom = 2.dp, end = 16.dp)
                        .width(characterColumnWidth),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = player.name,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(characterColumnWidth),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (displayCharacter.icon != 0) {
                        Image(
                            painter = painterResource(id = displayCharacter.icon),
                            contentDescription = displayCharacter.name,
                            modifier = Modifier
                                .size(72.dp)
                                .aspectRatio(1f)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(72.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("?")
                        }
                    }
                    Column {
                        val nameColor = if (character.alignment == CharAlignment.GOOD) GoodPrimary else EvilPrimary
                        Text(
                            text = displayCharacter.name,
                            style = MaterialTheme.typography.bodyLarge,
                            color = nameColor,
                            softWrap = true
                        )
                        if (surprise != null) {
                            Text(
                                text = "IS THE ${character.name.uppercase(getDefault())}",
                                style = MaterialTheme.typography.labelSmall,
                                color = nameColor,
                                textAlign = TextAlign.Center
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
}
