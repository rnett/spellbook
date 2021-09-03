package com.rnett.spellbook

import java.util.Locale

fun Color.asCompose(): androidx.compose.ui.graphics.Color {
    if (isTransparent)
        return androidx.compose.ui.graphics.Color.Transparent
    return androidx.compose.ui.graphics.Color(
        ("FF" + this.hexString.trim('#')).lowercase(Locale.getDefault()).toLong(16)
    )
        .copy(alpha = alpha)
}