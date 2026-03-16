package com.example.clockplucker.data

import androidx.compose.ui.graphics.Path
import java.util.UUID

data class Script(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val author: String,
    val characters: List<Character> = emptyList()
)