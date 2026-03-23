package com.example.clockplucker.data

import androidx.annotation.DrawableRes

data class Character(
    val id: String,
    val name: String,
    val type: CharType,
    val alignment: CharAlignment = if (type == CharType.TOWNSFOLK || type == CharType.OUTSIDER) CharAlignment.GOOD else CharAlignment.EVIL,
    @DrawableRes val icon: Int,
    val ability: String,
    val maxInstances: Int = 1, // for the Village Idiot
    val dependsOn: String? = null, // for the Huntsman and Choirboy
    val hardJinxedWith: List<String> = emptyList(), // for the Heretic
    val modifierOptions: List<SetupModifier> = listOf(SetupModifier()), // for the Balloonist, Baron, etc.
    val isSelectable: Boolean = true // for the Drunk, Lunatic, and Marionette
)

enum class CharType {
    TOWNSFOLK, OUTSIDER, MINION, DEMON
}

enum class CharAlignment {
    GOOD, EVIL
}