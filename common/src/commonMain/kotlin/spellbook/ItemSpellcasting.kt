package com.rnett.spellbook.spellbook

import com.rnett.spellbook.spell.Spell
import kotlinx.serialization.Serializable

@Serializable
data class ItemSpellcasting(
    val spells: Map<Spell, Int> = emptyMap()
)