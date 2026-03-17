package com.example.clockplucker.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import com.example.clockplucker.HelpButton
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.NDropdown
import com.example.clockplucker.NavigationBar
import com.example.clockplucker.SectionHeader
import com.example.clockplucker.data.CharAlignment
import com.example.clockplucker.data.CharType

@Composable
fun OptionsScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    var helpText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                progress = 2,
                onBack = onBack,
                onNext = onNext
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            SectionHeader("GARDENING MODE")

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
                            .clickable { viewModel.selectedMode = i }
                            .padding(vertical = 12.dp)
                    ) {
                        RadioButton(
                            selected = viewModel.selectedMode == i,
                            onClick = { viewModel.selectedMode = i }
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
                            " ${viewModel.alignmentN.ifEmpty { "n" }} "
                        )

                        3 -> pair.second.replace(
                            " n ",
                            " ${viewModel.typeN.ifEmpty { "n" }} "
                        )

                        else -> pair.second
                    }
                    HelpButton(onClick = { helpText = explanation })
                }
            }

            SectionHeader("STORYTELLER PRIORITIES")

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
                            .clickable { viewModel.selectedPriority = i }
                            .padding(vertical = 12.dp)
                    ) {
                        RadioButton(
                            selected = viewModel.selectedPriority == i,
                            onClick = { viewModel.selectedPriority = i }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = pair.first, style = MaterialTheme.typography.bodyMedium)
                    }
                    HelpButton(onClick = { helpText = pair.second })
                }
            }

            SectionHeader("") // Divider with no label

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
