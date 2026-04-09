package clockplucker

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

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import clockplucker.data.CharType
import clockplucker.data.Character
import clockplucker.data.Player
import clockplucker.data.Script
import clockplucker.data.ScriptLoader
import clockplucker.data.local.SavedScript
import clockplucker.data.local.ScriptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SelectedModes {
    NO_RESTRICTIONS, ALIGNMENT, TYPE;

    companion object {
        fun fromInt(i: Int): SelectedModes {
            return when (i) {
                1 -> NO_RESTRICTIONS
                2 -> ALIGNMENT
                3 -> TYPE
                else -> throw IllegalArgumentException("Invalid value for SelectedModes: $i")
            }
        }
        fun toInt(mode: SelectedModes): Int {
            return when (mode) {
                NO_RESTRICTIONS -> 1
                ALIGNMENT -> 2
                TYPE -> 3
            }
        }
    }
}

enum class SelectedPriorities {
    NO_PRIORITIES, ALIGNMENT, TYPE;

    companion object {
        fun fromInt(i: Int): SelectedPriorities {
            return when (i) {
                1 -> NO_PRIORITIES
                2 -> ALIGNMENT
                3 -> TYPE
                else -> throw IllegalArgumentException("Invalid value for SelectedPriorities: $i")
            }
        }
        fun toInt(mode: SelectedModes): Int {
            return when (mode) {
                SelectedModes.NO_RESTRICTIONS -> 1
                SelectedModes.ALIGNMENT -> 2
                SelectedModes.TYPE -> 3
            }
        }
    }
}

class MainViewModel(private val repository: ScriptRepository) : ViewModel() {
    private var _loadedScript by mutableStateOf<Script?>(null)
    var loadedScript: Script?
        get() = _loadedScript
        set(value) {
            _loadedScript = value
            if (value != null) {
                // If the Hermit is in the script, it may or may not think it is another character,
                //      depending on what other outsiders are in the script
                val updatedSelectableChars = value.selectableCharacters.map { character ->
                    if (character.id == "hermit") {
                        val drunk = value.selectableCharacters.any { it.id == "drunk" }
                        val lunatic = value.selectableCharacters.any { it.id == "lunatic" }
                        val hermitThinksTheyAre = when {
                            drunk && lunatic -> listOf(CharType.DEMON)
                            drunk -> listOf(CharType.TOWNSFOLK)
                            lunatic -> listOf(CharType.DEMON)
                            else -> character.thinksTheyAre
                        }
                        character.copy(thinksTheyAre = hermitThinksTheyAre)
                    } else character
                }
                _loadedScript = value.copy(selectableCharacters = updatedSelectableChars)

                // Default assignment chances for surprise characters to be 50%
                value.selectableCharacters
                    .filter { it.thinksTheyAre.isNotEmpty() }
                    .forEach { char ->
                        surpriseChance.putIfAbsent(char, 0.5f)
                    }
                if (value.containsSentinel) {
                    autoSentinel = true
                }
            }
        }

    val savedScripts: StateFlow<List<SavedScript>> =
        repository.allScripts.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val players = mutableStateListOf<Player>().apply {
        addAll(List(5) { Player() })
    }

    var selectedMode by mutableStateOf(SelectedModes.NO_RESTRICTIONS)
    var selectedPriority by mutableStateOf(SelectedPriorities.NO_PRIORITIES)

    var playerPriorityToggle by mutableStateOf(false)

    var alignmentN by mutableIntStateOf(1)
    var typeN by mutableIntStateOf(1)

    var autoSentinel by mutableStateOf(false)
    var manualSentinelModifier by mutableIntStateOf(0)

    var alchemistAbilityIndex by mutableIntStateOf(0)

    val surpriseChance = mutableStateMapOf<Character, Float>()

    fun updatePlayer(index: Int, player: Player) {
        if (index in players.indices) {
            players[index] = player
        }
    }

    fun removePlayer(index: Int) {
        if (index in players.indices) {
            players.removeAt(index)
        }
    }

    fun addPlayer() {
        players.add(Player())
    }

    fun updatePlayers(newPlayers: List<Player>) {
        players.clear()
        players.addAll(newPlayers)
    }

    fun movePlayer(fromIndex: Int, toIndex: Int) {
        if (fromIndex == toIndex) return
        if (fromIndex in players.indices && toIndex in players.indices) {
            players.add(toIndex, players.removeAt(fromIndex))
        }
    }

    fun saveScriptToHistory(script: Script, localPath: String) {
        viewModelScope.launch {
            repository.insert(
                SavedScript(
                    name = script.name,
                    author = script.author,
                    localPath = localPath
                )
            )
        }
    }

    fun loadSavedScript(context: Context, savedScript: SavedScript) {
        viewModelScope.launch {
            val json =
                context.openFileInput(savedScript.localPath).bufferedReader().use { it.readText() }
            loadedScript = ScriptLoader().parseScript(json)
        }
    }

    fun deleteScript(script: SavedScript) {
        viewModelScope.launch {
            repository.delete(script)
            if (loadedScript?.name == script.name && loadedScript?.author == script.author) {
                loadedScript = null
            }
        }
    }

    fun updateLastAccessed() {
        val currentScript = loadedScript ?: return
        viewModelScope.launch {
            repository.updateLastAccessed(currentScript.name, currentScript.author)
        }
    }
}
