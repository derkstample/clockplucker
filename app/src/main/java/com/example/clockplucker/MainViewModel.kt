package com.example.clockplucker

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clockplucker.data.Player
import com.example.clockplucker.data.Script
import com.example.clockplucker.data.ScriptLoader
import com.example.clockplucker.data.local.SavedScript
import com.example.clockplucker.data.local.ScriptRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(private val repository: ScriptRepository) : ViewModel() {
    var loadedScript by mutableStateOf<Script?>(null)
    val savedScripts: StateFlow<List<SavedScript>> =
        repository.allScripts.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    var players by mutableStateOf(List(5) { Player() })
        private set

    var selectedMode by mutableIntStateOf(1)
    var selectedPriority by mutableIntStateOf(1)

    var playerPriorityToggle by mutableStateOf(false)

    var alignmentN by mutableStateOf("1")
    var typeN by mutableStateOf("1")

    fun updatePlayer(index: Int, player: Player) {
        players = players.mapIndexed { i, p -> if (i == index) player else p }
    }

    fun removePlayer(index: Int) {
        players = players.filterIndexed { i, _ -> i != index }
    }

    fun addPlayer() {
        players = players + Player()
    }

    fun setScript(script: Script) {
        loadedScript = script
    }

    fun saveScriptToHistory(name: String, localPath: String){
        viewModelScope.launch {
            repository.insert(SavedScript(name = name, localPath = localPath))
        }
    }

    fun loadSavedScript(context: Context, savedScript: SavedScript) {
        val json = context.openFileInput(savedScript.localPath).bufferedReader().use { it.readText() }
        loadedScript = ScriptLoader().parseScript(json)
    }
}