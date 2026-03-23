package com.example.clockplucker.data

import java.util.UUID

data class Player(
    val id: UUID = UUID.randomUUID(),
    val name: String = "",
    val typePriority: CharType? = null, // we don't always need to prioritize types
    val alignmentPriority: CharAlignment? = null, // see above
    val selectedChars: List<Character> = emptyList(),
    val historyWeight: Int = 1
)