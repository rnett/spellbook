package com.rnett.spellbook.components.spellbooks

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.SpellSlotSpec

@Composable
fun ItemLevel(
    spells: SpellLevel.Item,
    knownLists: Set<SpellList>,
    level: Int,
    set: (SpellLevel.Item) -> Unit,
    openInfoDrawer: (Spell) -> Unit,
    searchSlot: (SpellSlotSpec, (Spell) -> Unit) -> Unit,
) {
    SpellcastingLevelDisplay(null, {

    }) {
        spells.spells.toList().sortedBy { it.first.name }.forEach {
            Text("Level $level")
        }
    }
}