package com.example.clockplucker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptDao {
    @Query("SELECT * FROM saved_scripts ORDER BY lastAccessed DESC")
    fun getAllScripts(): Flow<List<SavedScript>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScript(script: SavedScript)

    @Delete
    suspend fun deleteScript(script: SavedScript)

    @Update
    suspend fun updateScript(script: SavedScript)
}