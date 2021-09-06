package com.rnett.spellbook.spellbook

import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import kotlinx.serialization.Serializable

@Serializable
data class ItemSpellcasting(
    val spellLists: Set<SpellList> = emptySet(),
    val spells: Map<Spell, Int> = emptyMap()
)