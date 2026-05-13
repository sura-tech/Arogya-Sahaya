package com.arogya.sahaya.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val AppTypography = Typography(
    displayLarge  = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold,    lineHeight = 40.sp),
    headlineLarge = TextStyle(fontSize = 30.sp, fontWeight = FontWeight.Bold,    lineHeight = 36.sp),
    headlineMedium= TextStyle(fontSize = 26.sp, fontWeight = FontWeight.SemiBold,lineHeight = 32.sp),
    titleLarge    = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold,lineHeight = 28.sp),
    titleMedium   = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Medium,  lineHeight = 26.sp),
    bodyLarge     = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal,  lineHeight = 26.sp),
    bodyMedium    = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Normal,  lineHeight = 24.sp),
    bodySmall     = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal,  lineHeight = 22.sp),
    labelLarge    = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold,lineHeight = 24.sp),
    labelMedium   = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium,  lineHeight = 20.sp),
    labelSmall    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium,  lineHeight = 18.sp),
)
