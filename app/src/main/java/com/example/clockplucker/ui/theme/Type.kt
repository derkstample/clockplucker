package com.example.clockplucker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.clockplucker.R

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
