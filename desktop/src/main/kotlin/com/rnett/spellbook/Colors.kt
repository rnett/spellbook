package com.rnett.spellbook

fun Color.asCompose(): androidx.compose.ui.graphics.Color {
    if (isTransparent)
        return androidx.compose.ui.graphics.Color.Transparent
    return androidx.compose.ui.graphics.Color(("FF" + this.hexString.trim('#')).toLowerCase().toLong(16))
        .copy(alpha = alpha)
}