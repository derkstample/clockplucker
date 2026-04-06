package com.example.clockplucker.data

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.clockplucker.R

data class Character(
    val id: String,
    val name: TextValue,
    val ability: TextValue,
    val type: CharType,
    val alignment: CharAlignment = if (type == CharType.TOWNSFOLK || type == CharType.OUTSIDER) CharAlignment.GOOD else CharAlignment.EVIL,
    @DrawableRes val icon: Int = R.drawable.icon_bootlegger,
    val maxInstances: Int = 1, // for the Village Idiot
    val dependsOn: String? = null, // for the Huntsman and Choirboy
    val hardJinxedWith: List<String> = emptyList(), // for the Heretic
    val additiveModifiers: List<Count> = listOf(Count()), // for the Balloonist, Baron, etc.
    val overrideModifiers: List<CharType> = emptyList(), // for the Atheist, Legion, etc.
    val thinksTheyAre: List<CharType> = emptyList() // for the Drunk, Lunatic, and Marionette
)

enum class CharType {
    TOWNSFOLK, OUTSIDER, MINION, DEMON, FABLED, LORIC, TRAVELLER
}

enum class CharAlignment {
    GOOD, EVIL
}

sealed class TextValue {
    data class Resource(@StringRes val resId: Int) : TextValue()
    data class Raw(val text: String) : TextValue()

    @Composable
    fun asString(): String {
        return when (this) {
            is Resource -> stringResource(resId)
            is Raw -> text
        }
    }

    fun resolve(context: android.content.Context): String {
        return when (this) {
            is Resource -> context.getString(resId)
            is Raw -> text
        }
    }

    // Non-composable version for logging/debugging
    fun asRawString(context: android.content.Context? = null): String {
        return when (this) {
            is Resource -> context?.getString(resId) ?: "Resource ID: $resId"
            is Raw -> text
        }
    }
}