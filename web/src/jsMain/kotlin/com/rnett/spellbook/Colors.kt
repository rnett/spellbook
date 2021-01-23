package com.rnett.spellbook

fun Color.asCSS() = kotlinx.css.Color(this.hexString)