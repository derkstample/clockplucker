package com.example.clockplucker.data

import androidx.annotation.DrawableRes
import java.util.UUID

data class Character(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: CharType,
    val alignment: CharAlignment = if (type == CharType.TOWNSFOLK || type == CharType.OUTSIDER) CharAlignment.GOOD else CharAlignment.EVIL,
    @DrawableRes val icon: Int,
    val ability: String
)

enum class CharType {
    TOWNSFOLK, OUTSIDER, MINION, DEMON
}

enum class CharAlignment {
    GOOD, EVIL
}