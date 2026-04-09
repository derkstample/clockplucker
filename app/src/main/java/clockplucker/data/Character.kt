package clockplucker.data

//    Copyright 2026 Derek Rodriguez
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.

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