package com.rnett.spellbook.ui.components.spellbook.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rnett.spellbook.model.spellbook.Spellbook
import com.rnett.spellbook.model.spellbook.SpellcastingDef

@Composable
fun AddSpellcasting(spellbook: Spellbook, add: (SpellcastingDef) -> Unit) {
    Column {
        Text("Add Spellcasting")
    }
}