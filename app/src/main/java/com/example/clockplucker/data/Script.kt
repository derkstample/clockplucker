package com.example.clockplucker.data

import java.util.UUID

data class Script(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val author: String,
    val characters: List<Character> = emptyList(),
    val containsSentinel: Boolean = false,
    val containsPope: Boolean = false
)