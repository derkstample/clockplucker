package clockplucker.data

//    Copyright 2026 Derek Rodriguez
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <https://www.gnu.org/licenses/>.

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import clockplucker.R

data class Character(
    val id: String,
    var name: TextValue,
    var ability: TextValue,
    var type: CharType,
    var alignment: CharAlignment = if (type == CharType.TOWNSFOLK || type == CharType.OUTSIDER) CharAlignment.GOOD else CharAlignment.EVIL,
    @DrawableRes val icon: Int = R.drawable.icon_bootlegger,
    var maxInstances: Int = 1, // for the Village Idiot
    var dependsOn: String? = null, // for the Huntsman and Choirboy
    var hardJinxedWith: List<String> = emptyList(), // for the Heretic
    var additiveModifiers: List<Count> = listOf(Count()), // for the Balloonist, Baron, etc.
    var overrideModifiers: List<CharType> = emptyList(), // for the Atheist, Legion, etc.
    var thinksTheyAre: List<CharType> = emptyList() // for the Drunk, Lunatic, and Marionette
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Character) return false
        return id == other.id
    }
    override fun hashCode(): Int {
        return id.hashCode()
    }
}

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

    /**
     * Converts the TextValue into an AnnotatedString, preserving HTML-like tags
     * (like <b> or <i>) defined in strings.xml.
     */
    @SuppressLint("LocalContextGetResourceValueCall")
    @Composable
    fun asAnnotatedString(): AnnotatedString {
        return when (this) {
            is Raw -> AnnotatedString(text)
            is Resource -> {
                val context = LocalContext.current
                remember(resId) {
                    val charSequence = context.getText(resId)
                    if (charSequence is Spanned) {
                        buildAnnotatedString {
                            append(charSequence.toString())
                            charSequence.getSpans(0, charSequence.length, StyleSpan::class.java).forEach { span ->
                                val start = charSequence.getSpanStart(span)
                                val end = charSequence.getSpanEnd(span)
                                when (span.style) {
                                    Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                                    Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                                    Typeface.BOLD_ITALIC -> addStyle(
                                        SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic),
                                        start,
                                        end
                                    )
                                }
                            }
                        }
                    } else {
                        AnnotatedString(charSequence.toString())
                    }
                }
            }
        }
    }

    fun resolve(context: Context): String {
        return when (this) {
            is Resource -> context.getString(resId)
            is Raw -> text
        }
    }

    // Non-composable version for logging/debugging
    fun asRawString(context: Context? = null): String {
        return when (this) {
            is Resource -> context?.getString(resId) ?: "Resource ID: $resId"
            is Raw -> text
        }
    }
}