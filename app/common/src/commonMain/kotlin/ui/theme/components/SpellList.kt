package com.rnett.spellbook.ui.theme.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import com.rnett.spellbook.model.spell.SpellList


fun SpellList.color(): Color = when (this) {
    SpellList.Arcane -> Color.Blue
    SpellList.Divine -> Color.Yellow
    SpellList.Occult -> Color.Gray
    SpellList.Primal -> Color.Green
    SpellList.Elemental -> Color.Yellow.copy(alpha = 0.5f).compositeOver(Color.Red)
    SpellList.Focus -> Color.Green.copy(alpha = 0.5f).compositeOver(Color.Gray)
    SpellList.Other -> Color.Transparent
}