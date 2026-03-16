package com.example.clockplucker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_scripts")
data class SavedScript(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val localPath: String,
    val dateAdded: Long = System.currentTimeMillis(),
    val lastAccessed: Long = System.currentTimeMillis()
)
