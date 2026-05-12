package clockplucker

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
import clockplucker.data.CharacterTransformer
import clockplucker.data.Count
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
    NO_RESTRICTIONS, ALIGNMENT, TYPE, SPECIFY, NONE;

    companion object {
        fun fromInt(i: Int): SelectedModes {
            return when (i) {
                1 -> NO_RESTRICTIONS
                2 -> ALIGNMENT
                3 -> TYPE
                4 -> SPECIFY
                5 -> NONE
                else -> throw IllegalArgumentException("Invalid value for SelectedModes: $i")
            }
        }
        fun toInt(mode: SelectedModes): Int {
            return when (mode) {
                NO_RESTRICTIONS -> 1
                ALIGNMENT -> 2
                TYPE -> 3
                SPECIFY -> 4
                NONE -> 5
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
        fun toInt(mode: SelectedPriorities): Int {
            return when (mode) {
                NO_PRIORITIES -> 1
                ALIGNMENT -> 2
                TYPE -> 3
            }
        }
    }
}

class MainViewModel(private val repository: ScriptRepository) : ViewModel() {
    private var _loadedScript by mutableStateOf<Script?>(null)
    var loadedScript: Script?
        get() = _loadedScript
        set(value) {
            if (value != null) {
                val uniqueChars = value.selectableCharacters.map { it.copy() }
                uniqueChars.forEach { CharacterTransformer.applyScriptwideRules(it, uniqueChars) }
                _loadedScript = value.copy(selectableCharacters = uniqueChars)

                // Reset alchemist settings
                alchemistAbilityIndex = 0
                enableDuplicateMinionModifiers = false
                _alchemistJinx = null

                // Default assignment chances for surprise characters to be 50%
                value.selectableCharacters
                    .filter { it.thinksTheyAre.isNotEmpty() }
                    .forEach { char ->
                        surpriseChance.putIfAbsent(char, 0.5f)
                    }
                if (value.containsSentinel) {
                    autoSentinel = true
                }
            } else {
                _loadedScript = null
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

    var specifyN by mutableStateOf(Count(1,1,1,1))

    var autoSentinel by mutableStateOf(false)
    var manualSentinelModifier by mutableIntStateOf(0)

    var alchemistAbilityIndex by mutableIntStateOf(0)
    fun updateAlchemistAbilityIndex(index: Int) {
        alchemistAbilityIndex = index
        updateTransformedAlchemist()
    }

    var enableDuplicateMinionModifiers by mutableStateOf(false)
    fun updateEnableDuplicateMinionModifiers(enabled: Boolean) {
        enableDuplicateMinionModifiers = enabled
        updateTransformedAlchemist()
    }

    private fun updateTransformedAlchemist() {
        val script = loadedScript ?: return
        val alchemist = script.selectableCharacters.find { it.id == "alchemist" } ?: return
        val minions = script.selectableCharacters.filter { it.type == CharType.MINION }
        val minion = minions.getOrNull(alchemistAbilityIndex) ?: return

        CharacterTransformer.transformAlchemist(alchemist, minion, enableDuplicateMinionModifiers, _alchemistJinx)
    }

    private var _alchemistJinx: String? = null
    fun updateAlchemistJinx(jinx: String?) {
        _alchemistJinx = jinx
        updateTransformedAlchemist()
    }

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
