package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.Spellcasting

data object SpontaneousSpellcastingBuilder : SpellcastingBuilder {
    @Composable
    override fun Render(addButton: @Composable (Spellcasting?) -> Unit) {
        Column {
            var name by remember { mutableStateOf("") }
            var archetype by remember { mutableStateOf(false) }

            Row {
                SpellcastingName(name) { name = it }
                Spacer(Modifier.width(20.dp))
                ArchetypeSelector(archetype) { archetype = it }
            }
        }
    }
}