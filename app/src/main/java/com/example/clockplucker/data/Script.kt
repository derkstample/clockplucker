package com.example.clockplucker.data

import java.util.UUID

data class Script(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val author: String,
    val selectableCharacters: List<Character> = emptyList(),
    val excludedCharacters: List<Character> = emptyList()
) {
    val allCharacters: List<Character> get() = selectableCharacters + excludedCharacters
    val containsSentinel: Boolean get() = excludedCharacters.any { it.id == "sentinel" }
    val containsPope: Boolean get() = excludedCharacters.any { it.id == "pope" }
    val containsSurprises: Boolean get() = selectableCharacters.any { !it.thinksTheyAre.isEmpty() }
}
