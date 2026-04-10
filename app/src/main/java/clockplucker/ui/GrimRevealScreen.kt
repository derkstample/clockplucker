package clockplucker.ui

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

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import clockplucker.MainViewModel
import clockplucker.R
import clockplucker.SectionHeader
import clockplucker.SelectedPriorities
import clockplucker.data.CharAlignment
import clockplucker.data.CharType
import clockplucker.data.Character
import clockplucker.drawStableVerticalScrollbar
import clockplucker.data.Count
import clockplucker.data.Player
import clockplucker.data.RoleSolver
import clockplucker.data.TypeCountLookup
import clockplucker.ui.theme.EvilPrimary
import clockplucker.ui.theme.GoodPrimary
import java.util.Locale.getDefault
import kotlinx.coroutines.launch

@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun GrimRevealScreen(
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val script = viewModel.loadedScript
    val characters = script?.selectableCharacters ?: emptyList()
    val players = viewModel.players
    val lookup = remember { TypeCountLookup() }
    val containsPope = remember(script) { script?.containsPope ?: false }
    val sentinelModifier = remember(viewModel.autoSentinel, viewModel.manualSentinelModifier) {
        if (viewModel.autoSentinel) Count()
        else when (viewModel.manualSentinelModifier) {
            1 -> Count(townsfolk = -1, outsider = 1)
            -1 -> Count(townsfolk = 1, outsider = -1)
            else -> Count()
        }
    }

    var showExitDialog by remember { mutableStateOf(false) }
    var showRegenDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    BackHandler {
        showExitDialog = true
    }

    // Custom saver for the assignments state
    val assignmentsSaver = remember(players, characters) {
        listSaver<MutableState<Map<Player, Pair<Character, Character?>>?>, String>(
            save = { state ->
                state.value?.map { (player, pair) ->
                    "${player.id}|${pair.first.id}|${pair.second?.id ?: ""}"
                } ?: emptyList()
            },
            restore = { strings ->
                mutableStateOf(
                    if (strings.isEmpty()) null
                    else strings.associate { str ->
                        val parts = str.split("|")
                        val player = players.find { it.id.toString() == parts[0] }!!
                        val char = characters.find { it.id == parts[1] }!!
                        val surprise = parts.getOrNull(2)?.takeIf { it.isNotEmpty() }?.let { id ->
                            characters.find { it.id == id }
                        }
                        player to (char to surprise)
                    }
                )
            }
        )
    }

    val assignmentsState = rememberSaveable(saver = assignmentsSaver) {
        mutableStateOf<Map<Player, Pair<Character, Character?>>?>(null)
    }
    
    // Use revealed state that survives rotation
    var revealed by rememberSaveable {
        mutableStateOf(false)
    }
    
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(revealed) {
        // Only calculate if we don't already have saved assignments
        if (assignmentsState.value == null && script != null) {
            val solver = RoleSolver(
                players = players,
                availableChars = characters,
                baseCount = lookup.getBaseCounts(players.size) + sentinelModifier,
                surpriseChances = viewModel.surpriseChance,
                selectedPriority = viewModel.selectedPriority,
                playerPriorityToggle = viewModel.playerPriorityToggle,
                containsPope = containsPope,
                autoSentinel = viewModel.autoSentinel
            )
            assignmentsState.value = solver.optimizeAssignments()
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.bodyLarge
    val density = LocalDensity.current
    val context = LocalContext.current
    val maxNameWidth = remember(assignmentsState.value, context) {
        val currentAssignments = assignmentsState.value ?: return@remember 160.dp
        val maxWidthPx = currentAssignments.values.maxOf { (char, surprise) ->
            val charName = char.name.resolve(context)
            val surpriseName = surprise?.name?.resolve(context)
            val words = charName.split(Regex("\\s+")) + (surpriseName?.split(Regex("\\s+")) ?: emptyList())
            words.maxOf { word ->
                textMeasurer.measure(
                    text = word,
                    style = labelStyle,
                    maxLines = 1
                ).size.width
            }
        }
        // 72dp for character icon, 24dp for chevron, 12dp for paddings/margins
        with(density) { (maxWidthPx.toDp() + 108.dp) }
    }

    val listState = rememberLazyListState()

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.return_to_script_selection)) },
            text = { Text(stringResource(R.string.return_to_script_selection_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        // update player historyWeights
                        val currentAssignments = assignmentsState.value
                        if (currentAssignments != null) {
                            viewModel.players.toList().forEachIndexed { index, player ->
                                val assignedChar = currentAssignments[player]?.first // note that we only care about the first character, since a drunk empath didn't really get the full empath experience
                                val wasMappedToSelected = assignedChar?.let { char ->
                                    player.selectedChars.any { it.id == char.id }
                                } ?: false

                                val newWeight = if (wasMappedToSelected) 1 else player.historyWeight + 1
                                // note that if a player is deleted and readded, their historyWeight won't be preserved
                                // this is fine, as historyWeight is a hidden feature only really intended for single sessions
                                viewModel.updatePlayer(index, player.copy(historyWeight = newWeight))
                            }
                        }
                        onNext()
                    }
                ) {
                    Text(stringResource(R.string.yes), style = MaterialTheme.typography.bodyMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(stringResource(R.string.no), style = MaterialTheme.typography.bodyMedium)
                }
            }
        )
    }

    if (showRegenDialog) {
        AlertDialog(
            onDismissRequest = { showRegenDialog = false },
            title = { Text(stringResource(R.string.regenerate_grimoire)) },
            text = { Text(stringResource(R.string.regenerate_grimoire_desc)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRegenDialog = false
                        assignmentsState.value = null // Clear assignments to show the waiting animation again
                        scope.launch {
                            if (script != null) {
                                val solver = RoleSolver(
                                    players = players,
                                    availableChars = characters,
                                    baseCount = lookup.getBaseCounts(players.size) + sentinelModifier,
                                    surpriseChances = viewModel.surpriseChance,
                                    selectedPriority = viewModel.selectedPriority,
                                    playerPriorityToggle = viewModel.playerPriorityToggle,
                                    containsPope = containsPope,
                                    autoSentinel = viewModel.autoSentinel
                                )
                                assignmentsState.value = solver.optimizeAssignments()
                            }
                        }
                    }
                ) {
                    Text(stringResource(R.string.yes), style = MaterialTheme.typography.bodyMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRegenDialog = false }) {
                    Text(stringResource(R.string.no), style = MaterialTheme.typography.bodyMedium)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (revealed) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 4.dp, bottom = 4.dp)
                                .size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.player_received_a_preferred_character),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 4.dp, bottom = 4.dp)
                                .size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.player_thinks_they_received_a_preferred_character),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Row(
                        modifier = Modifier
                            .windowInsetsPadding(WindowInsets.navigationBars)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val showRegen = script?.containsSurprises == true &&
                                viewModel.surpriseChance.values.any { it > 0f && it < 1f }

                        if (showRegen) {
                            OutlinedButton(
                                onClick = { showRegenDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = stringResource(R.string.regenerate),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        OutlinedButton(
                            onClick = { showExitDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.restart),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SectionHeader(
                text = stringResource(R.string.grimoire),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            val currentAssignments = assignmentsState.value
            if (revealed && currentAssignments != null) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                        .drawStableVerticalScrollbar(state = listState)
                ) {
                    itemsIndexed(
                        items = currentAssignments.toList(),
                        key = { _, assignment -> assignment.first.id }
                    ) { index, assignment ->
                        GrimRow(
                            player = assignment.first,
                            pair = assignment.second,
                            index = index,
                            characterColumnWidth = maxNameWidth,
                            viewModel = viewModel
                        )
                    }
                }
            } else if (revealed) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        WaitingAnimation(
                            numPlayers = players.size,
                            modifier = Modifier.padding(vertical = 24.dp)
                        )
                        Text(
                            text = stringResource(R.string.calculating),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = stringResource(R.string.please_wait),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontSize = MaterialTheme.typography.labelSmall.fontSize
                            )
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
                        primary = MaterialTheme.colorScheme.primary
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
    primary: Color
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
                text = stringResource(R.string.reveal_grimoire),
                style = MaterialTheme.typography.labelLarge,
                color = primary
            )
            Text(
                text = stringResource(R.string.tap_and_hold),
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
    characterColumnWidth: Dp,
    viewModel: MainViewModel
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "Rotation"
    )

    val character = remember(pair) { pair.first }
    val surprise = remember(pair) { pair.second }

    val displayCharacter = surprise ?: character
    val isSelectedMatch = remember(player.selectedChars, character) {
        player.selectedChars.any { it.id == character.id }
    }
    val isSelectedMatchWithSurprise = remember(player.selectedChars, surprise) {
        player.selectedChars.any { it.id == surprise?.id }
    }

    val name = displayCharacter.name.asAnnotatedString()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = { expanded = !expanded }),
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
                    text = stringResource(R.string.player_index_label, index + 1),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .padding(start = 16.dp, bottom = 2.dp)
                        .weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )
                Row(
                    modifier = Modifier
                        .padding(bottom = 2.dp, end = 16.dp)
                        .width(characterColumnWidth),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.character),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (isSelectedMatch) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 4.dp, bottom = 4.dp)
                                .size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    } else if (isSelectedMatchWithSurprise) {
                        Icon(
                            imageVector = Icons.Default.StarBorder,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(start = 4.dp, bottom = 4.dp)
                                .size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
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
                Box(
                    modifier = Modifier
                        .padding(end = 16.dp)
                        .width(characterColumnWidth)
                ) {
                    Row(
                        modifier = Modifier.align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = displayCharacter.icon),
                            contentDescription = name.toString(),
                            modifier = Modifier
                                .size(72.dp)
                                .aspectRatio(1f)
                        )
                        Column (
                            modifier = Modifier.padding(vertical = 8.dp)
                        ){
                            val nameColor = if (character.alignment == CharAlignment.GOOD) GoodPrimary else EvilPrimary
                            Row (
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = nameColor,
                                    softWrap = true,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .rotate(rotation),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (surprise != null) {
                                Text(
                                    text = stringResource(
                                        R.string.is_the,
                                        character.name.asString().uppercase(getDefault())
                                    ),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = nameColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 10.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.selected_characters),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            if (player.selectedChars.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.no_characters_selected),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                player.selectedChars.forEach { char ->
                                    val name = char.name.asAnnotatedString()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Image(
                                            painter = painterResource(id = char.icon),
                                            contentDescription = name.toString(),
                                            modifier = Modifier.size(28.8.dp)
                                        )
                                        val charColor = if (char.alignment == CharAlignment.GOOD) GoodPrimary else EvilPrimary
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text(
                                                text = name,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    fontSize = MaterialTheme.typography.bodySmall.fontSize * 1.2f
                                                ),
                                                color = charColor
                                            )
                                            if (char.id == character.id) {
                                                Icon(
                                                    imageVector = Icons.Default.Star,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .padding(start = 4.dp, bottom = 4.dp)
                                                        .size(16.dp),
                                                    tint = charColor
                                                )
                                            } else if (char.id == surprise?.id) {
                                                Icon(
                                                    imageVector = Icons.Default.StarBorder,
                                                    contentDescription = null,
                                                    modifier = Modifier
                                                        .padding(start = 4.dp, bottom = 4.dp)
                                                        .size(16.dp),
                                                    tint = charColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (viewModel.selectedPriority == SelectedPriorities.ALIGNMENT) {
                            Column(
                                modifier = Modifier.width(characterColumnWidth),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = stringResource(R.string.prioritized_alignment),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                val priorityText = when (player.alignmentPriority) {
                                    CharAlignment.GOOD -> stringResource(R.string.good)
                                    CharAlignment.EVIL -> stringResource(R.string.evil)
                                    null -> stringResource(R.string.any)
                                }
                                val priorityColor = when (player.alignmentPriority) {
                                    CharAlignment.GOOD -> GoodPrimary
                                    CharAlignment.EVIL -> EvilPrimary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = priorityText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = priorityColor
                                    )
                                    if (character.alignment == player.alignmentPriority) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(start = 4.dp, bottom = 4.dp)
                                                .size(16.dp),
                                            tint = priorityColor
                                        )
                                    } else if (surprise != null && surprise.alignment == player.alignmentPriority) {
                                        Icon(
                                            imageVector = Icons.Default.StarBorder,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(start = 4.dp, bottom = 4.dp)
                                                .size(16.dp),
                                            tint = priorityColor
                                        )
                                    }
                                }
                            }
                        } else if (viewModel.selectedPriority == SelectedPriorities.TYPE) {
                            Column(
                                modifier = Modifier.width(characterColumnWidth),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = stringResource(R.string.prioritized_type),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                val priorityText = when (player.typePriority) {
                                    CharType.TOWNSFOLK -> stringResource(R.string.townsfolk)
                                    CharType.OUTSIDER -> stringResource(R.string.outsider_s, "")
                                    CharType.MINION -> stringResource(R.string.minion_s, "")
                                    CharType.DEMON -> stringResource(R.string.demon_s, "")
                                    else -> stringResource(R.string.any)
                                }
                                val priorityColor = when (player.typePriority) {
                                    CharType.TOWNSFOLK, CharType.OUTSIDER -> GoodPrimary
                                    CharType.MINION, CharType.DEMON -> EvilPrimary
                                    else -> MaterialTheme.colorScheme.onSurface
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = priorityText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = priorityColor
                                    )
                                    if (character.type == player.typePriority) {
                                        Icon(
                                            imageVector = Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(start = 4.dp, bottom = 4.dp)
                                                .size(16.dp),
                                            tint = priorityColor
                                        )
                                    } else if (surprise != null && surprise.type == player.typePriority) {
                                        Icon(
                                            imageVector = Icons.Default.StarBorder,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .padding(start = 4.dp, bottom = 4.dp)
                                                .size(16.dp),
                                            tint = priorityColor
                                        )
                                    }
                                }
                            }
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
