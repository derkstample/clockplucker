package com.example.clockplucker.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.clockplucker.MainViewModel
import com.example.clockplucker.data.Character
import com.example.clockplucker.data.Player
import com.example.clockplucker.data.RoleSolver
import com.example.clockplucker.data.TypeCountLookup

@Composable
fun GrimRevealScreen(
    onBack: () -> Unit,
    onNext: () -> Unit,
    viewModel: MainViewModel
) {
    val characters = viewModel.loadedScript?.characters
    val players = viewModel.players
    val lookup = remember { TypeCountLookup() }

    // List of (TimeLimit, AssignmentMap)
    val assignments by produceState<List<Pair<String, Map<Player, Pair<Character, Character?>>>>?>(
        initialValue = null,
        characters,
        players.toList(),
        viewModel.selectedPriority,
        viewModel.playerPriorityToggle
    ) {
        if (characters != null) {
            // Shuffle only once to minimize variability across runs
            val shuffledPlayers = players.toList().shuffled()
            val shuffledCharacters = characters.shuffled()
            val limits = listOf("1s", "5s", "10s", "20s", "30s")
            val currentResults = mutableListOf<Pair<String, Map<Player, Pair<Character, Character?>>>>()
            
            for (limit in limits) {
                val solver = RoleSolver(
                    players = shuffledPlayers,
                    availableChars = shuffledCharacters,
                    baseCount = lookup.getBaseCounts(players.size),
                    unselectableChance = 0f,
                    selectedPriority = viewModel.selectedPriority,
                    playerPriorityToggle = viewModel.playerPriorityToggle
                )
                val result = solver.optimizeAssignments()
                currentResults.add(limit to result)
                // Update the state so the user can see progress
                value = currentResults.toList()
            }
        } else {
            value = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Grim Reveal (Time Limit Test)",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        when {
            assignments == null -> {
                Text(
                    text = "Initializing calculation...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            assignments!!.isEmpty() && characters != null -> {
                Text(
                    text = "Starting solve runs...",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            assignments!!.isEmpty() -> {
                Text(
                    text = "No valid assignment found for ${players.size} players.",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
            else -> {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    assignments!!.forEach { (limit, assignmentMap) ->
                        item(key = limit) {
                            Text(
                                text = "Time Limit: $limit",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        if (assignmentMap.isEmpty()) {
                            item {
                                Text(
                                    text = "No valid assignment found within $limit.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        } else {
                            items(assignmentMap.toList()) { (player, character) ->
                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Text(
                                        text = "${player.name.ifBlank { "Player" }}: ",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = character.first.name ?: "Unknown",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    if (player.selectedChars.contains(character.first)) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            modifier = Modifier.padding(start = 4.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "wanted: ${player.selectedChars.joinToString { it.name }}",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                    if (assignments!!.size < 5) {
                        item {
                            Text(
                                text = "Calculating next limit... (${assignments!!.size}/5 complete)",
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Button(onClick = onBack, modifier = Modifier.padding(end = 8.dp)) {
                Text("Back")
            }
            Button(onClick = onNext) {
                Text("Next")
            }
        }
    }
}
