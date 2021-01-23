package com.rnett.spellbook

fun Color.asCompose(): androidx.compose.ui.graphics.Color {
    if(hexString.toLowerCase() == "transparent" || hexString.isBlank())
        return androidx.compose.ui.graphics.Color.Transparent
    return androidx.compose.ui.graphics.Color(("FF" + this.hexString.trim('#')).toLowerCase().toLong(16))
}