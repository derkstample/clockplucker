package com.example.clockplucker

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockplucker.data.Character
import com.example.clockplucker.data.Player
import com.example.clockplucker.data.Script
import com.example.clockplucker.data.ScriptLoader
import com.example.clockplucker.data.local.SavedScript
import com.example.clockplucker.data.local.ScriptRepository
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
    var loadedScript by mutableStateOf<Script?>(null)
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
    var sentinelMod by mutableIntStateOf(0)

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
            val savedScript = savedScripts.value.find {
                it.name == currentScript.name && it.author == currentScript.author
            }
            savedScript?.let {
                repository.update(it.copy(lastAccessed = System.currentTimeMillis()))
            }
        }
    }
}
