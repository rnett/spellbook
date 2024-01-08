package com.rnett.spellbook.spellbook

import com.rnett.spellbook.spell.Spell
import kotlinx.serialization.Serializable

@Serializable
data class FocusSpellcasting(
    val cantrips: Set<Spell> = emptySet(),
    val spells: Set<Spell> = emptySet(),
    val maxPoints: Int = 1
)