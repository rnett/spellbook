package com.rnett.spellbook.components.spellbooks

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.rnett.spellbook.pages.SpellSearch
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.ItemSpellcasting

@Composable
fun ItemSpells(
    name: String,
    spells: ItemSpellcasting,
    set: (ItemSpellcasting) -> Unit,
    search: SpellSearch,
    showInfo: (Spell) -> Unit
) {
    //TODO implement
    Text("TODO: Implement")
}