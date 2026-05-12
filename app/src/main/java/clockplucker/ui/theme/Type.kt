package clockplucker.ui.theme

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

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import clockplucker.R

val DumbledoreFont = FontFamily(Font(R.font.dumbledor3))
val SortsMillGoudyFont = FontFamily(Font(R.font.sorts_mill_goudy))
val SortsMillGoudyItalicFont = FontFamily(Font(R.font.sorts_mill_goudy_italic))
val TradeGothicFont = FontFamily(Font(R.font.trade_gothic_lt_std))



// Set of Material typography styles to start with
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontFamily = TradeGothicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = TradeGothicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = TradeGothicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.5.sp
    ),

    titleLarge = TextStyle(
        fontFamily = SortsMillGoudyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SortsMillGoudyFont,
        fontWeight = FontWeight.Normal,
        fontSize = 26.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = SortsMillGoudyItalicFont,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontFamily = DumbledoreFont,
        fontWeight = FontWeight.Medium,
        fontSize = 36.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.75.sp
    ),
    labelMedium = TextStyle(
        fontFamily = DumbledoreFont,
        fontWeight = FontWeight.Medium,
        fontSize = 26.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.75.sp
    ),
    labelSmall = TextStyle(
        fontFamily = DumbledoreFont,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.75.sp
    )
)
