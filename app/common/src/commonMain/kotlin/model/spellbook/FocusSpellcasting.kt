package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.Spell
import kotlinx.serialization.Serializable

@Serializable
data class FocusSpellcasting(
    val cantrips: Set<Spell> = emptySet(),
    val spells: Set<Spell> = emptySet(),
    val maxPoints: Int = 1
)