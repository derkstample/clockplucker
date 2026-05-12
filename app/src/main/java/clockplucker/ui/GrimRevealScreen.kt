package clockplucker.ui

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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
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
import clockplucker.data.CharacterRepository
import clockplucker.drawStableVerticalScrollbar
import clockplucker.data.Count
import clockplucker.data.Player
import clockplucker.data.RoleSolver
import clockplucker.data.TypeCountLookup
import clockplucker.ui.theme.EvilPrimary
import clockplucker.ui.theme.GoodPrimary
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Locale.getDefault
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

//TODO: Alchemist should show its ability. In the GrimRow, any assigned Alchemist should have the
//      text "HAS THE [name]'S ABILITY", where [name] is the name of the Alchemist's ability minion.
//      In the case of a Drunk who thinks they are the Alchemist, the GrimRow should show Alchemist
//      with "HAS THE [names]'S ABILITY" underneath and "IS THE DRUNK" underneath that.


@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun GrimRevealScreen(
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val script = viewModel.loadedScript
    val characters = script?.selectableCharacters ?: emptyList()
    val players = viewModel.players
    val lookup = remember { TypeCountLookup() }
    val containsPope = remember(script) { script?.containsPope ?: false }

    val demonGroups = remember(context) {
        val demons = CharacterRepository.allCharacters.filter { it.type == CharType.DEMON }
        val groups = mutableMapOf<String, MutableSet<String>>()

        demons.forEach { demon ->
            val ability = demon.ability.resolve(context)
            val groupId = when {
                // note: the order we check for substrings actually matters-- the Po contains both
                //      "may choose" and "choose 3". If we switch the order of the checks,
                //      the Po is in the same group as the Al-Hadikhia
                ability.contains("may choose a player") -> "may_choose"
                ability.contains("choose 3 players") -> "choose_3"
                ability.contains("choose 2 players") -> "choose_2"
                ability.contains("choose a player") ||
                        ability.contains("a player might die") -> "choose_1_or_die"
                else -> "singleton_${demon.id}"
            }
            groups.getOrPut(groupId) { mutableSetOf() }.add(demon.id)
        }
        groups.values.map { it.toSet() }
    }

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
                            if (id.startsWith("lilmonsta_")) {
                                val minionId = id.removePrefix("lilmonsta_")
                                (characters.find { it.id == minionId } ?: CharacterRepository.getCharacterInfo(minionId))?.copy(id = id)
                            } else {
                                characters.find { it.id == id }
                            }
                        }
                        player to (char to surprise)
                    }
                )
            }
        )
    }

    val assignmentsState = rememberSaveable(saver = assignmentsSaver) {
        mutableStateOf(null)
    }

    var solverProgress by remember { mutableStateOf<RoleSolver.SolverProgress?>(null) }
    
    // Use revealed state that survives rotation
    var revealed by rememberSaveable {
        mutableStateOf(false)
    }
    
    val haptic = LocalHapticFeedback.current

    val flavortextList = remember(script, players) {
        val list = mutableListOf<String>()
        val charactersInScript = script?.selectableCharacters ?: emptyList()
        val characterIds = charactersInScript.map { it.id }.toSet()

        // Generic 0-25
        for (i in 0..25) {
            val resId = context.resources.getIdentifier("generation_flavortext_$i", "string", context.packageName)
            if (resId != 0) list.add(context.getString(resId))
        }

        // Script-dependent
        if (script != null) {
            if (!characterIds.contains("heretic")) {
                list.add(context.getString(R.string.generation_flavortext_26))
            }
            if (script.containsSentinel) {
                list.add(context.getString(R.string.generation_flavortext_27))
                list.add(context.getString(R.string.generation_flavortext_28))
            }
            if (characterIds.contains("legion") && characterIds.contains("magician")) {
                list.add(context.getString(R.string.generation_flavortext_29))
            }
            if (characterIds.contains("damsel")) {
                val randomPlayer = players.randomOrNull()?.name ?: context.getString(R.string.unknown_player)
                list.add(context.getString(R.string.generation_flavortext_30, randomPlayer))
            }
            if (script.name != "Bad Moon Rising") {
                list.add(context.getString(R.string.generation_flavortext_31))
            }
            if (characterIds.contains("tealady")) list.add(context.getString(R.string.generation_flavortext_32))
            if (characterIds.contains("wizard")) list.add(context.getString(R.string.generation_flavortext_33))
            if (characterIds.contains("mathematician")) list.add(context.getString(R.string.generation_flavortext_34))
            if (characterIds.contains("shugenja")) list.add(context.getString(R.string.generation_flavortext_35))
            if (characterIds.contains("scarletwoman")) list.add(context.getString(R.string.generation_flavortext_36))
            if (characterIds.contains("villageidiot")) list.add(context.getString(R.string.generation_flavortext_37))
            if (characterIds.contains("mastermind")) list.add(context.getString(R.string.generation_flavortext_38))
            if (characterIds.contains("mayor")) list.add(context.getString(R.string.generation_flavortext_39))
            if (characterIds.contains("atheist")) {
                list.add(context.getString(R.string.generation_flavortext_40))
                if (characterIds.contains("drunk")) list.add(context.getString(R.string.generation_flavortext_41))
            }
            if (characterIds.contains("mutant")) list.add(context.getString(R.string.generation_flavortext_42))
            if (characterIds.contains("imp")) list.add(context.getString(R.string.generation_flavortext_43))
            if (characterIds.contains("cannibal")) list.add(context.getString(R.string.generation_flavortext_44))
            if (characterIds.contains("mutant") || characterIds.contains("cerenovus")) list.add(context.getString(R.string.generation_flavortext_45))
            if (characterIds.contains("poppygrower")) list.add(context.getString(R.string.generation_flavortext_46))

            // Dynamic
            val allChars = CharacterRepository.allCharacters
            val charactersNotInScript = allChars.filter { !characterIds.contains(it.id) }

            charactersNotInScript.randomOrNull()?.let {
                list.add(context.getString(R.string.generation_flavortext_47, it.name.resolve(context)))
            }
            charactersInScript.randomOrNull()?.let {
                list.add(context.getString(R.string.generation_flavortext_48, it.name.resolve(context)))
            }
            players.randomOrNull()?.let {
                list.add(context.getString(R.string.generation_flavortext_49, it.name))
                list.add(context.getString(R.string.generation_flavortext_50, it.name))
            }
            charactersInScript.randomOrNull()?.let {
                list.add(context.getString(R.string.generation_flavortext_51, it.name.resolve(context)))
                list.add(context.getString(R.string.generation_flavortext_52, it.name.resolve(context)))
            }
        }
        list.shuffled()
    }

    var currentFlavortextIndex by remember { mutableIntStateOf(0) }
    var currentFlavortext by remember { mutableStateOf("") }
    var shuffledFlavortexts by remember(flavortextList) { mutableStateOf(flavortextList) }

    LaunchedEffect(assignmentsState.value, revealed, flavortextList) {
        if (assignmentsState.value == null && revealed) {
            while (isActive) {
                if (currentFlavortextIndex >= shuffledFlavortexts.size) {
                    shuffledFlavortexts = shuffledFlavortexts.shuffled()
                    currentFlavortextIndex = 0
                }
                currentFlavortext = shuffledFlavortexts[currentFlavortextIndex]
                currentFlavortextIndex++
                delay(3000) // 3 seconds per flavortext
            }
        }
    }

    LaunchedEffect(script) { // was using revealed as the key, but that regenerates once the reveal button is clicked, kinda defeating the purpose
        // Only calculate if we don't already have saved assignments
        if (assignmentsState.value == null && script != null) {
            generateAssignments(
                viewModel = viewModel,
                players = players,
                characters = characters,
                lookup = lookup,
                sentinelModifier = sentinelModifier,
                containsPope = containsPope,
                demonGroups = demonGroups,
                assignmentsState = assignmentsState,
                onProgressUpdate = { solverProgress = it }
            )
        }
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.bodyLarge
    val density = LocalDensity.current
    val maxNameWidth = remember(assignmentsState.value, context) {
        val currentAssignments = assignmentsState.value ?: return@remember 160.dp
        val maxWidthPx = currentAssignments.values.maxOfOrNull { (char, surprise) ->
            val charName = char.name.resolve(context)
            val surpriseName = surprise?.name?.resolve(context)
            val words = charName.split(Regex("\\s+")) + (surpriseName?.split(Regex("\\s+")) ?: emptyList())
            words.maxOfOrNull { word ->
                textMeasurer.measure(
                    text = word,
                    style = labelStyle,
                    maxLines = 1
                ).size.width
            } ?: 0
        } ?: 0
        // 72dp for character icon, 24dp for chevron, 12dp for paddings/margins
        with(density) { (maxWidthPx.toDp() + 108.dp) }
    }

    val listState = rememberLazyListState()
    val showTopFade by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 0
        }
    }

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
                            generateAssignments(
                                viewModel = viewModel,
                                players = players,
                                characters = characters,
                                lookup = lookup,
                                sentinelModifier = sentinelModifier,
                                containsPope = containsPope,
                                demonGroups = demonGroups,
                                assignmentsState = assignmentsState,
                                onProgressUpdate = { solverProgress = it }
                            )
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
                        OutlinedButton(
                            onClick = { showRegenDialog = true },
                            modifier = Modifier.weight(1f),
                            enabled = assignmentsState.value != null
                        ) {
                            Text(
                                text = stringResource(R.string.regenerate),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
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
                        .graphicsLayer {
                            compositingStrategy = if (showTopFade) CompositingStrategy.Offscreen else CompositingStrategy.Auto
                        }
                        .drawWithContent {
                            drawContent() // Draw the actual list items first

                            if (showTopFade) {
                                // Define the fading area
                                val fadeHeight = 40.dp.toPx()

                                // Draw a gradient that masks the content
                                drawRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black
                                        ),
                                        startY = 0f,
                                        endY = fadeHeight
                                    ),
                                    blendMode = BlendMode.DstIn
                                )
                            }
                        }
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

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
                        Spacer(modifier = Modifier.padding(vertical = 32.dp))
                        if (currentFlavortext.isNotEmpty()) {
                            Text(
                                text = currentFlavortext,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary,
                                fontStyle = FontStyle.Italic,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.padding(vertical = 120.dp))
                        Text(
                            text = "Nodes Explored: " + solverProgress?.nodesExplored,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Solutions Found: " + solverProgress?.solutionsFound,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Time Elapsed: " + solverProgress?.timeElapsed,
                            style = MaterialTheme.typography.bodySmall
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

//TODO: if the generated demon is a lilmonsta and a marionette has been generated,
//      we must change the real character of the marionette player to whatever
//      character they think they are. Note that RoleSolver will guarantee that
//      whatever they think they are will be unassigned to any other players,
//      ensuring uniqueness. In rendering the assignments on the GrimRow, the
//

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
                                    text = if (character.id == "lilmonsta" || surprise.id.startsWith("lilmonsta_")) {
                                        stringResource(R.string.has_the_lil_monsta)
                                    } else {
                                        stringResource(
                                            R.string.is_the,
                                            character.name.asString().uppercase(getDefault())
                                        )
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = nameColor,
                                    textAlign = TextAlign.Center
                                )
                                if (surprise.id.startsWith("lilmonsta_") && character.id != "lilmonsta") {
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
                                    CharType.TOWNSFOLK -> stringResource(R.string.townsfolk).uppercase()
                                    CharType.OUTSIDER -> stringResource(R.string.outsider_s, "").uppercase()
                                    CharType.MINION -> stringResource(R.string.minion_s, "").uppercase()
                                    CharType.DEMON -> stringResource(R.string.demon_s, "").uppercase()
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

private suspend fun generateAssignments(
    viewModel: MainViewModel,
    players: List<Player>,
    characters: List<Character>,
    lookup: TypeCountLookup,
    sentinelModifier: Count,
    containsPope: Boolean,
    demonGroups: List<Set<String>>,
    assignmentsState: MutableState<Map<Player, Pair<Character, Character?>>?>,
    onProgressUpdate: (RoleSolver.SolverProgress?) -> Unit
) {
    if (viewModel.loadedScript == null) return

    while (true) {
        var shouldRetry = false
        val solver = RoleSolver(
            players = players,
            availableChars = characters.shuffled(),
            baseCount = lookup.getBaseCounts(players.size) + sentinelModifier,
            surpriseChances = viewModel.surpriseChance,
            selectedPriority = viewModel.selectedPriority,
            playerPriorityToggle = viewModel.playerPriorityToggle,
            containsPope = containsPope,
            autoSentinel = viewModel.autoSentinel
        )

        coroutineScope {
            val solverJob = async(Dispatchers.Default) {
                solver.optimizeAssignments { progress ->
                    onProgressUpdate(progress)
                }
            }

            val progressJob = launch {
                while (isActive) {
                    solver.getProgress()?.let { progress ->
                        onProgressUpdate(progress)
                        if (progress.solutionsFound == 0 && progress.timeElapsed > 20) {
                            shouldRetry = true
                            solver.stop()
                            solverJob.cancel("Solver stuck")
                        }
                    }
                    delay(1000)
                }
            }

            try {
                val results = solverJob.await()
                val realDemons = results.values.map { it.first }.filter { it.type == CharType.DEMON }
                val adjustedResults = if (realDemons.isNotEmpty()) {
                    val targetGroup = realDemons.mapNotNull { demon ->
                        demonGroups.find { it.contains(demon.id) }
                    }.randomOrNull()

                    if (targetGroup != null) {
                        results.mapValues { (player, pair) ->
                            val (real, fake) = pair
                            if ((real.id == "lunatic" || real.id == "hermit") && fake?.type == CharType.DEMON) {
                                val availableInGroup = characters.filter { it.type == CharType.DEMON && targetGroup.contains(it.id) }
                                val preferredFakes = availableInGroup.filter { player.selectedChars.contains(it) }
                                val newFakeId = if (preferredFakes.isNotEmpty()) { // first try to assign a preferred char
                                    preferredFakes.random().id
                                } else { // otherwise just assign a random demon from the demon group
                                    availableInGroup.random().id
                                }
                                val newFake = characters.find { it.id == newFakeId }
                                    ?: CharacterRepository.getCharacterInfo(newFakeId)
                                    ?: fake
                                real to newFake
                            } else pair
                        }
                    } else results
                } else results

                // Handle Lil' Monsta assignment after demonic grouping
                val finalResults = if (adjustedResults.any { it.value.first.id == "lilmonsta" || it.value.second?.id == "lilmonsta" }) {
                    val allValidMinions = characters.filter { it.type == CharType.MINION && it.thinksTheyAre.isEmpty() }
                    val assignedMinionIds = adjustedResults.values.map { it.first.id }.toSet()
                    val unassignedMinions = allValidMinions.filter { !assignedMinionIds.contains(it.id) }

                    // in the case that the only unassigned minion is a surprise character (marionette),
                    // they cannot hold the lil' monsta, both because they think they are good, and because
                    // the neighboring the demon condition is not guaranteed. Therefore, I'm just regenerating
                    // the whole grim, not sure if there's a much better solution.
                    if (unassignedMinions.isEmpty()) shouldRetry = true

                    val lilMonstaPlayers = adjustedResults.filter { it.value.first.id == "lilmonsta" || it.value.second?.id == "lilmonsta" }
                    val preferredMinion = lilMonstaPlayers.keys.flatMap { it.selectedChars }.filter { it.type == CharType.MINION }.find { unassignedMinions.contains(it) }
                        ?: unassignedMinions.randomOrNull()
                        ?: allValidMinions.randomOrNull()

                    if (preferredMinion != null) {
                        val lilMinion = preferredMinion.copy(id = "lilmonsta_${preferredMinion.id}")
                        adjustedResults.mapValues { (_, pair) ->
                            val (real, fake) = pair
                            if (real.id == "lilmonsta") {
                                real to lilMinion
                            } else if (fake?.id == "lilmonsta") {
                                real to lilMinion
                            } else pair
                        }
                    } else adjustedResults
                } else adjustedResults

                assignmentsState.value = finalResults
            } catch (e: CancellationException) {
                if (e.message != "Solver stuck") throw e
            } finally {
                progressJob.cancel()
                solver.getProgress()?.let { onProgressUpdate(it) }
            }
        }
        if (!shouldRetry) break
    }
}
