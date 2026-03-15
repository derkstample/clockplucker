package com.example.clockplucker

import androidx.annotation.DrawableRes

data class Character(
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