package com.example.clockplucker.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.clockplucker.HelpButton
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NDropdown
import com.example.clockplucker.NavigationBar
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
    var helpText by remember { mutableStateOf<String?>(null) }
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
            SectionHeader("SELECTION OPTIONS")

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
                                val maxAlignment = viewModel.loadedScript?.characters?.let { chars ->
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
                                Text(text = " Of Each Alignment", style = MaterialTheme.typography.bodyMedium)
                            }

                            3 -> {
                                val maxType = viewModel.loadedScript?.characters?.let { chars ->
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
                                Text(text = " Of Each Type", style = MaterialTheme.typography.bodyMedium)
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

            Spacer(modifier = Modifier.weight(1f))
            SectionHeader(text = "STORYTELLER PRIORITIES")

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
                            .clickable { viewModel.selectedPriority = SelectedPriorities.fromInt(i) }
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

            Spacer(modifier = Modifier.weight(1f))
            SectionHeader("PLAYER PRIORITIES") // Divider with no label

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { viewModel.playerPriorityToggle = !viewModel.playerPriorityToggle }
                        .padding(vertical = 12.dp)
                ) {
                    Switch(
                        checked = viewModel.playerPriorityToggle,
                        onCheckedChange = { viewModel.playerPriorityToggle = it }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Enable Player Priorities", style = MaterialTheme.typography.bodyMedium)
                }
                HelpButton(onClick = {
                    helpText = "Players are allowed to prioritize certain selected characters over others in a ranked list."
                })
            }

            if (viewModel.loadedScript?.containsSentinel == true) {
                Spacer(modifier = Modifier.weight(1f))
                SectionHeader("SENTINEL OPTIONS")

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
                            Text(text = " Outsiders", style = MaterialTheme.typography.bodyMedium)
                        }
                        val sentinelHelpText = when (viewModel.sentinelMod) {
                            1 -> "This script contains a Sentinel. This option forces the Sentinel to add 1 Outsider, removing 1 Townsfolk in the process."
                            -1 -> "This script contains a Sentinel. This option forces the Sentinel to remove 1 Outsider, adding 1 Townsfolk in the process."
                            else -> "This script contains a Sentinel. This option forces the Sentinel to have no effect on the number of Outsiders in the game."
                        }
                        HelpButton(onClick = { helpText = sentinelHelpText })
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
                            Text(text = "Automatic", style = MaterialTheme.typography.bodyMedium)
                        }
                        HelpButton(onClick = {
                            helpText = "This script contains a Sentinel. This option allows the Sentinel to automatically determine the best option to use when assigning characters to each player."
                        })
                    }
                }
            }

            val surpriseChars = viewModel.loadedScript?.characters?.filter { it.thinksTheyAre.isNotEmpty() } ?: emptyList()
            if (surpriseChars.isNotEmpty()) {
                Spacer(modifier = Modifier.weight(1f))
                SectionHeader("SURPRISE CHARACTERS")

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Surprise character chance: ${(viewModel.surpriseChance * 100).roundToInt()}%",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        val surpriseNames = when (surpriseChars.size) {
                            1 -> surpriseChars.first().name
                            2 -> surpriseChars.joinToString(separator = " and ") { it.name }
                            else -> surpriseChars.dropLast(1).joinToString(separator = ", ") { it.name } + ", and " + surpriseChars.last().name
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        HelpButton(onClick = {
                            helpText = when (surpriseChars.size) {
                                1 -> "The $surpriseNames is designed such that players cannot select it as preferred. Therefore, it can only be assigned if forced to, which will happen which will happen with a ${(viewModel.surpriseChance * 100).roundToInt()}% chance."
                                else -> "The $surpriseNames are designed such that players cannot select them as preferred. Therefore, they can only be assigned if forced to, which will happen with a ${(viewModel.surpriseChance * 100).roundToInt()}% chance."
                            }
                        })
                    }
                    Slider(
                        value = viewModel.surpriseChance,
                        onValueChange = { viewModel.surpriseChance = it },
                        valueRange = 0f..1f,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    helpText?.let { text ->
        AlertDialog(
            onDismissRequest = { helpText = null },
            confirmButton = {
                TextButton(onClick = { helpText = null }) {
                    Text(text = "OK", style = MaterialTheme.typography.bodyMedium)
                }
            },
            text = { Text(text) }
        )
    }
}
