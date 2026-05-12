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

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import clockplucker.HelpButton
import clockplucker.MainViewModel
import clockplucker.NDropdown
import clockplucker.NavigationBar
import clockplucker.R
import clockplucker.SectionHeader
import clockplucker.SelectedModes
import clockplucker.SelectedPriorities
import clockplucker.data.CharAlignment
import clockplucker.data.CharType
import clockplucker.data.Count
import clockplucker.data.DjinnRepository
import clockplucker.drawStableVerticalScrollbar
import clockplucker.ui.theme.EvilPrimary
import kotlin.collections.set
import kotlin.math.roundToInt

@Composable
fun OptionsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    var helpText by rememberSaveable { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                onBack = onBack,
                onNext = onNext,
                progress = 2,
                total = 3
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .drawStableVerticalScrollbar(state = scrollState)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            SelectionOptionsRow(
                viewModel = viewModel,
                onHelpTextChange = { helpText = it }
            )

            StoryTellerPrioritiesRow(
                viewModel = viewModel,
                onHelpTextChange = { helpText = it }
            )

            PlayerPrioritiesRow(
                viewModel = viewModel,
                onHelpTextChange = { helpText = it }
            )

            SentinelSettingsRow(
                viewModel = viewModel,
                onAutoHelpTextChange = { helpText = it },
                onManualHelpTextChange = { plusOneText, minusOneText, zeroText ->
                    helpText = when (viewModel.manualSentinelModifier) {
                        1 -> plusOneText
                        -1 -> minusOneText
                        else -> zeroText
                    }
                }
            )

            SurpriseCharactersRow(
                viewModel = viewModel,
                onHelpTextChange = { helpText = it }
            )

            AlchemistSelectionRow(
                viewModel = viewModel,
                onHelpTextChange = { helpText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    helpText?.let { text ->
        AlertDialog(
            onDismissRequest = { helpText = null },
            confirmButton = {
                TextButton(onClick = { helpText = null }) {
                    Text(text = stringResource(R.string.ok), style = MaterialTheme.typography.bodyMedium)
                }
            },
            text = { Text(text) }
        )
    }
}

@Composable
fun SelectionOptionsRow(
    viewModel: MainViewModel,
    onHelpTextChange: (String) -> Unit
) {
    SectionHeader(stringResource(R.string.option_selection_mode))
    Spacer(modifier = Modifier.height(16.dp))

    val modes = listOf(
        stringResource(R.string.mode_no_restrictions) to stringResource(R.string.mode_no_restrictions_desc),
        stringResource(R.string.mode_n_alignment, viewModel.alignmentN) to stringResource(R.string.mode_n_alignment_desc, viewModel.alignmentN, if (viewModel.alignmentN > 1) "s" else ""),
        stringResource(R.string.mode_n_type, viewModel.typeN) to stringResource(R.string.mode_n_type_desc, viewModel.typeN, if (viewModel.typeN > 1) "s" else ""),
        stringResource(R.string.mode_specify) to stringResource(R.string.mode_specify_desc,
            viewModel.specifyN.townsfolk,
            viewModel.specifyN.outsider, if (viewModel.specifyN.outsider > 1) "s" else "",
            viewModel.specifyN.minion, if (viewModel.specifyN.minion > 1) "s" else "",
            viewModel.specifyN.demon, if (viewModel.specifyN.demon > 1) "s" else ""
            ),
        stringResource(R.string.mode_none) to stringResource(R.string.mode_none_desc)
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
                    .clip(MaterialTheme.shapes.large)
                    .clickable { viewModel.selectedMode = SelectedModes.fromInt(i) }
                    .padding(vertical = 12.dp)
            ) {
                RadioButton(
                    selected = viewModel.selectedMode == SelectedModes.fromInt(i),
                    onClick = { viewModel.selectedMode = SelectedModes.fromInt(i) },
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))

                when (i) {
                    2 -> {
                        val maxAlignment = viewModel.loadedScript?.selectableCharacters?.let { chars ->
                            val good = chars.count { it.alignment == CharAlignment.GOOD }
                            val evil = chars.count { it.alignment == CharAlignment.EVIL }
                            maxOf(good, evil)
                        } ?: 1 // loadedScript should never be null here, but failsafe regardless

                        NDropdown(
                            value = viewModel.alignmentN,
                            onValueChange = {
                                viewModel.alignmentN = it
                                viewModel.selectedMode =
                                    SelectedModes.fromInt(
                                        i
                                    )
                            },
                            min = 1,
                            max = maxAlignment
                        )
                        Text(text = stringResource(R.string.n_alignment_dropdown), style = MaterialTheme.typography.bodyMedium)
                    }

                    3 -> {
                        val maxType = viewModel.loadedScript?.selectableCharacters?.let { chars ->
                            val townsfolk = chars.count { it.type == CharType.TOWNSFOLK }
                            val outsider = chars.count { it.type == CharType.OUTSIDER }
                            val minion = chars.count { it.type == CharType.MINION }
                            val demon = chars.count { it.type == CharType.DEMON }
                            maxOf(townsfolk, maxOf(outsider, maxOf(minion, demon)))
                        } ?: 1

                        NDropdown(
                            value = viewModel.typeN,
                            onValueChange = {
                                viewModel.typeN = it
                                viewModel.selectedMode =
                                    SelectedModes.fromInt(
                                        i
                                    )
                            },
                            min = 1,
                            max = maxType
                        )
                        Text(text = stringResource(R.string.n_type_dropdown), style = MaterialTheme.typography.bodyMedium)
                    }

                    4 -> {
                        val maxCount = viewModel.loadedScript?.selectableCharacters?.let { chars ->
                            val townsfolk = chars.count { it.type == CharType.TOWNSFOLK }
                            val outsider = chars.count { it.type == CharType.OUTSIDER }
                            val minion = chars.count { it.type == CharType.MINION }
                            val demon = chars.count { it.type == CharType.DEMON }
                            Count(townsfolk, outsider, minion, demon)
                        } ?: Count(1,1,1,1)

                        FlowRow (
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                NDropdown(
                                    value = viewModel.specifyN.townsfolk,
                                    onValueChange = {
                                        viewModel.specifyN = viewModel.specifyN.copy(townsfolk = it)
                                        viewModel.selectedMode = SelectedModes.SPECIFY
                                    },
                                    min = 0,
                                    max = maxCount.townsfolk
                                )
                                Text(text = stringResource(R.string.townsfolk) + ",", style = MaterialTheme.typography.bodyMedium)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                NDropdown(
                                    value = viewModel.specifyN.outsider,
                                    onValueChange = {
                                        viewModel.specifyN = viewModel.specifyN.copy(outsider = it)
                                        viewModel.selectedMode = SelectedModes.SPECIFY
                                    },
                                    min = 0,
                                    max = maxCount.outsider
                                )
                                Text(text = stringResource(R.string.outsider_s, if (viewModel.specifyN.outsider != 1) "s," else ","), style = MaterialTheme.typography.bodyMedium)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(end = 8.dp)
                            ) {
                                NDropdown(
                                    value = viewModel.specifyN.minion,
                                    onValueChange = {
                                        viewModel.specifyN = viewModel.specifyN.copy(minion = it)
                                        viewModel.selectedMode = SelectedModes.SPECIFY
                                    },
                                    min = 0,
                                    max = maxCount.minion
                                )
                                Text(text = stringResource(R.string.minion_s, if (viewModel.specifyN.minion != 1) "s," else ","), style = MaterialTheme.typography.bodyMedium)
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NDropdown(
                                    value = viewModel.specifyN.demon,
                                    onValueChange = {
                                        viewModel.specifyN = viewModel.specifyN.copy(demon = it)
                                        viewModel.selectedMode = SelectedModes.SPECIFY
                                    },
                                    min = 0,
                                    max = maxCount.demon
                                )
                                Text(text = stringResource(R.string.demon_s, if (viewModel.specifyN.demon != 1) "s" else ""), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    else -> {
                        Text(text = pair.first, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
            HelpButton(onClick = { onHelpTextChange(pair.second) })
        }
        if (index < modes.size - 1) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun StoryTellerPrioritiesRow(
    viewModel: MainViewModel,
    onHelpTextChange: (String) -> Unit
) {
    SectionHeader(text = stringResource(R.string.option_storyteller_priorities))
    Spacer(modifier = Modifier.height(16.dp))

    val priorities = listOf(
        stringResource(R.string.priority_no_priorities) to stringResource(R.string.priority_no_priorities_desc),
        stringResource(R.string.priority_alignment) to stringResource(R.string.priority_alignment_desc),
        stringResource(R.string.priority_type) to stringResource(R.string.priority_type_desc)
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
                    .clip(MaterialTheme.shapes.large)
                    .clickable { viewModel.selectedPriority = SelectedPriorities.fromInt(i) }
                    .padding(vertical = 12.dp)
            ) {
                RadioButton(
                    selected = viewModel.selectedPriority == SelectedPriorities.fromInt(i),
                    onClick = { viewModel.selectedPriority = SelectedPriorities.fromInt(i) },
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = pair.first, style = MaterialTheme.typography.bodyMedium)
            }
            HelpButton(onClick = { onHelpTextChange(pair.second) })
        }
        if (index < 2) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun PlayerPrioritiesRow(
    viewModel: MainViewModel,
    onHelpTextChange: (String) -> Unit
) {
    val playerPriorityHelp = stringResource(R.string.player_priority_desc)
    SectionHeader(stringResource(R.string.option_player_priorities))
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(1f)
                .clip(MaterialTheme.shapes.large)
                .clickable { viewModel.playerPriorityToggle = !viewModel.playerPriorityToggle }
                .padding(vertical = 12.dp)
        ) {
            Switch(
                checked = viewModel.playerPriorityToggle,
                onCheckedChange = { viewModel.playerPriorityToggle = it },
                modifier = Modifier.padding(start = 8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = stringResource(R.string.player_priority), style = MaterialTheme.typography.bodyMedium)
        }
        HelpButton(onClick = { onHelpTextChange(playerPriorityHelp) })
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SentinelSettingsRow(
    viewModel: MainViewModel,
    onAutoHelpTextChange: (String) -> Unit,
    onManualHelpTextChange: (String, String, String) -> Unit
) {
    if (viewModel.loadedScript?.containsSentinel == true) {
        val sentinelHelpAutoText = stringResource(R.string.sentinel_help_auto)
        val plusOneText = stringResource(R.string.sentinel_help_plusone)
        val minusOneText = stringResource(R.string.sentinel_help_minusone)
        val zeroText = stringResource(R.string.sentinel_help_zero)

        SectionHeader(stringResource(R.string.option_sentinel))
        Spacer(modifier = Modifier.height(16.dp))
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.large)
                        .clickable { viewModel.autoSentinel = true }
                        .padding(vertical = 12.dp)
                ) {
                    RadioButton(
                        selected = viewModel.autoSentinel,
                        onClick = { viewModel.autoSentinel = true },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = stringResource(R.string.sentinel_label_automatic), style = MaterialTheme.typography.bodyMedium)
                }
                HelpButton(onClick = {
                    onAutoHelpTextChange(
                        sentinelHelpAutoText
                    )
                })
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.large)
                        .clickable { viewModel.autoSentinel = !viewModel.autoSentinel }
                        .padding(vertical = 12.dp)
                ) {
                    RadioButton(
                        selected = !viewModel.autoSentinel,
                        onClick = { viewModel.autoSentinel = !viewModel.autoSentinel },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))

                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Text(
                            text = when (viewModel.manualSentinelModifier) {
                                1 -> "+1"
                                -1 -> "-1"
                                else -> "+0"
                            },
                            modifier = Modifier
                                .clickable { expanded = true }
                                .padding(horizontal = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                textDecoration = TextDecoration.Underline,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            listOf(
                                "+1" to 1,
                                "+0" to 0,
                                "-1" to -1
                            ).forEach { (label, mode) ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = label,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    onClick = {
                                        viewModel.manualSentinelModifier = mode
                                        viewModel.autoSentinel = false
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                    Text(
                        text = " " + stringResource(
                            R.string.sentinel_label,
                            if (viewModel.manualSentinelModifier == 0) "s" else ""
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                HelpButton(onClick = {
                    onManualHelpTextChange(
                        plusOneText,
                        minusOneText,
                        zeroText
                    )
                })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SurpriseCharactersRow(
    viewModel: MainViewModel,
    onHelpTextChange: (String) -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val surpriseChars = viewModel.loadedScript?.selectableCharacters?.filter { it.thinksTheyAre.isNotEmpty() } ?: emptyList()
    if (surpriseChars.isNotEmpty()) {
        SectionHeader(stringResource(R.string.option_surprise_chars))
        Spacer(modifier = Modifier.height(16.dp))
        surpriseChars.forEachIndexed { index, char ->
            val chance = viewModel.surpriseChance[char] ?: 0.5f
            val charName = char.name.asString()

            val surpriseDesc = stringResource(
                R.string.surprise_desc,
                charName,
                (chance * 100).roundToInt()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            R.string.assignment_chance,
                            charName,
                            (chance * 100).roundToInt()
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    HelpButton(onClick = {
                        onHelpTextChange(
                            surpriseDesc
                        )
                    })
                }
                Slider(
                    value = chance,
                    onValueChange = { newValue ->
                        val steppedValue = (newValue * 20).roundToInt() / 20f
                        if (steppedValue != chance) {
                            viewModel.surpriseChance[char] = steppedValue
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        }
                    },
                    valueRange = 0f..1f,
                    steps = 19,
                    modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                )
            }
            if (index < surpriseChars.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun AlchemistSelectionRow(
    viewModel: MainViewModel,
    onHelpTextChange: (String) -> Unit
) {
    if (viewModel.loadedScript?.containsAlchemist == true) {
        val minions = viewModel.loadedScript?.selectableCharacters?.filter { it.type == CharType.MINION } ?: emptyList()

        val selectedMinion = minions.getOrNull(viewModel.alchemistAbilityIndex) ?: minions.firstOrNull() ?: return
        val alchemistJinx = DjinnRepository.getJinxAbility("alchemist", selectedMinion.id)?.let { stringResource(it) }

        LaunchedEffect(alchemistJinx) {
            viewModel.updateAlchemistJinx(alchemistJinx)
        }

        val alchemistAbilityText = alchemistJinx ?: selectedMinion.ability.asString()

        val alchemistAbilityDesc = stringResource(R.string.alchemist_settings_desc, selectedMinion.name.asAnnotatedString())
        val alchemistDuplicateText = stringResource(R.string.alchemist_duplicate_switch_desc, selectedMinion.name.asAnnotatedString())

        SectionHeader(stringResource(R.string.alchemist_settings))
        Spacer(modifier = Modifier.height(16.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                var expanded by remember { mutableStateOf(false) }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.large)
                        .clickable { expanded = true }
                ) {
                    Column {
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(selectedMinion.icon),
                                    contentDescription = selectedMinion.name.asString(),
                                    modifier = Modifier
                                        .size(72.dp)
                                        .aspectRatio(1f)
                                )
                                Text(
                                    text = selectedMinion.name.asAnnotatedString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        textDecoration = TextDecoration.Underline,
                                        color = EvilPrimary
                                    )
                                )
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                minions.forEach { minion ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Image(
                                                    painter = painterResource(minion.icon),
                                                    contentDescription = minion.name.asString(),
                                                    modifier = Modifier
                                                        .size(72.dp)
                                                        .aspectRatio(1f)
                                                )
                                                Text(
                                                    text = minion.name.asAnnotatedString(),
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        color = EvilPrimary
                                                    ),
                                                    modifier = Modifier.padding(end = 12.dp)
                                                )
                                            }
                                        },
                                        onClick = {
                                            viewModel.updateAlchemistAbilityIndex(minions.indexOf(minion))
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                        Text(
                            text = alchemistAbilityText,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                HelpButton(onClick = { onHelpTextChange(alchemistAbilityDesc) })
            }
            val duplicateSwitchEnabled = remember(alchemistJinx, selectedMinion) {
                when {
                    // There is no jinx for this (yet), but Alchemist-Summoner surely must prevent
                    // there from being a real Summoner, or else one would always effectively be
                    // drunk because their ability is immediately overridden by the other's
                    selectedMinion.id == "summoner" -> {
                        viewModel.updateEnableDuplicateMinionModifiers(false)
                        false
                    }

                    alchemistJinx == null -> true
                    alchemistJinx.contains("not-in-play") -> {
                        viewModel.updateEnableDuplicateMinionModifiers(false)
                        false
                    }
                    alchemistJinx.contains("in play") -> {
                        viewModel.updateEnableDuplicateMinionModifiers(true)
                        false
                    }
                    else -> true
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.large)
                        .clickable(enabled = duplicateSwitchEnabled)
                        { viewModel.updateEnableDuplicateMinionModifiers(!viewModel.enableDuplicateMinionModifiers) }
                        .padding(vertical = 12.dp)
                ) {
                    Switch(
                        checked = viewModel.enableDuplicateMinionModifiers,
                        enabled = duplicateSwitchEnabled,
                        onCheckedChange = { viewModel.updateEnableDuplicateMinionModifiers(it) },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(R.string.alchemist_duplicate_switch), style = MaterialTheme.typography.bodyMedium)
                }
                HelpButton(onClick = { onHelpTextChange(alchemistDuplicateText) })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// TODO: Boffin-Balloonist switch