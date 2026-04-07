package com.example.clockplucker.data.local

import kotlinx.coroutines.flow.Flow

class ScriptRepository(private val scriptDao: ScriptDao) {
    val allScripts: Flow<List<SavedScript>> = scriptDao.getAllScripts()

    suspend fun insert(script: SavedScript) {
        scriptDao.insertScript(script)
    }

    suspend fun delete(script: SavedScript) {
        scriptDao.deleteScript(script)
    }

    suspend fun update(script: SavedScript) {
        scriptDao.updateScript(script)
    }

    suspend fun updateLastAccessed(name: String, author: String) {
        scriptDao.updateLastAccessed(name, author)
    }
}
