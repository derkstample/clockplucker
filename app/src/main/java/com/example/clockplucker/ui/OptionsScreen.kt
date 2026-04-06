package com.example.clockplucker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.clockplucker.HelpButton
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NDropdown
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.R
import com.example.clockplucker.SectionHeader
import com.example.clockplucker.SelectedModes
import com.example.clockplucker.SelectedPriorities
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType
import com.example.clockplucker.drawStableVerticalScrollbar
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
            SectionHeader(stringResource(R.string.option_selection_mode))

            val modes = listOf(
                stringResource(R.string.mode_no_restrictions) to stringResource(R.string.mode_no_restrictions_desc),
                stringResource(R.string.mode_n_alignment) to stringResource(R.string.mode_n_alignment_desc),
                stringResource(R.string.mode_n_type) to stringResource(R.string.mode_n_type_desc)
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
                            .clickable { viewModel.selectedMode = SelectedModes.fromInt(i) }
                            .padding(vertical = 12.dp)
                    ) {
                        RadioButton(
                            selected = viewModel.selectedMode == SelectedModes.fromInt(i),
                            onClick = { viewModel.selectedMode = SelectedModes.fromInt(i) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        when (i) {
                            2 -> {
                                val maxAlignment = viewModel.loadedScript?.selectableCharacters?.let { chars ->
                                    val good = chars.count { it.alignment == CharAlignment.GOOD }
                                    val evil = chars.count { it.alignment == CharAlignment.EVIL }
                                    maxOf(good, evil)
                                } ?: 10 // loadedScript should never be null here, but failsafe regardless

                                NDropdown(
                                    value = viewModel.alignmentN,
                                    onValueChange = { viewModel.alignmentN = it },
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
                                } ?: 10

                                NDropdown(
                                    value = viewModel.typeN,
                                    onValueChange = { viewModel.typeN = it },
                                    min = 1,
                                    max = maxType
                                )
                                Text(text = stringResource(R.string.n_type_dropdown), style = MaterialTheme.typography.bodyMedium)
                            }

                            else -> {
                                Text(text = pair.first, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }

                    val explanation = when (i) {
                        2 -> pair.second.replace(
                            " n ",
                            " ${viewModel.alignmentN} "
                        )

                        3 -> pair.second.replace(
                            " n ",
                            " ${viewModel.typeN} "
                        )

                        else -> pair.second
                    }
                    HelpButton(onClick = { helpText = explanation })
                }
                if (index < 2) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(text = stringResource(R.string.option_storyteller_priorities))

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
                            .clickable {
                                viewModel.selectedPriority = SelectedPriorities.fromInt(i)
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        RadioButton(
                            selected = viewModel.selectedPriority == SelectedPriorities.fromInt(i),
                            onClick = { viewModel.selectedPriority = SelectedPriorities.fromInt(i) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = pair.first, style = MaterialTheme.typography.bodyMedium)
                    }
                    HelpButton(onClick = { helpText = pair.second })
                }
                if (index < 2) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(stringResource(R.string.option_player_priorities))

            val playerPriorityHelp = stringResource(R.string.player_priority_desc)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            viewModel.playerPriorityToggle = !viewModel.playerPriorityToggle
                        }
                        .padding(vertical = 12.dp)
                ) {
                    Switch(
                        checked = viewModel.playerPriorityToggle,
                        onCheckedChange = { viewModel.playerPriorityToggle = it }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = stringResource(R.string.player_priority), style = MaterialTheme.typography.bodyMedium)
                }
                HelpButton(onClick = { helpText = playerPriorityHelp })
            }

            if (viewModel.loadedScript?.containsSentinel == true) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(stringResource(R.string.option_sentinel))

                val sentinelHelpAutoText = stringResource(R.string.sentinel_help_auto)
                val plusOneText = stringResource(R.string.sentinel_help_plusone)
                val minusOneText = stringResource(R.string.sentinel_help_minusone)
                val zeroText = stringResource(R.string.sentinel_help_zero)

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { viewModel.autoSentinel = false }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = !viewModel.autoSentinel,
                                onClick = { viewModel.autoSentinel = false }
                            )
                            Spacer(modifier = Modifier.width(12.dp))

                            var expanded by remember { mutableStateOf(false) }
                            Box {
                                Text(
                                    text = (if (viewModel.sentinelMod >= 0) "+" else "") + viewModel.sentinelMod.toString(),
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
                                    listOf(1, 0, -1).forEach { n ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = (if (n >= 0) "+" else "") + n.toString(),
                                                    style = MaterialTheme.typography.labelMedium
                                                )
                                            },
                                            onClick = {
                                                viewModel.sentinelMod = n
                                                viewModel.autoSentinel = false
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Text(text = stringResource(R.string.sentinel_label), style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        HelpButton(onClick = { 
                            helpText = when (viewModel.sentinelMod) {
                                1 -> plusOneText
                                -1 -> minusOneText
                                else -> zeroText
                            }
                        })
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
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
                                .clickable { viewModel.autoSentinel = true }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = viewModel.autoSentinel,
                                onClick = { viewModel.autoSentinel = true }
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = stringResource(R.string.sentinel_label_automatic), style = MaterialTheme.typography.bodyMedium)
                        }
                        HelpButton(onClick = { helpText = sentinelHelpAutoText })
                    }
                }
            }

            val surpriseChars = viewModel.loadedScript?.selectableCharacters?.filter { it.thinksTheyAre.isNotEmpty() } ?: emptyList()
            if (surpriseChars.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                SectionHeader(stringResource(R.string.option_surprise_chars))

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
                            HelpButton(onClick = { helpText = surpriseDesc })
                        }
                        Slider(
                            value = chance,
                            onValueChange = { viewModel.surpriseChance[char] = it },
                            valueRange = 0f..1f,
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
