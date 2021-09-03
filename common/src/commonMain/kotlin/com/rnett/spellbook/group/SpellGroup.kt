package com.rnett.spellbook.group

import com.rnett.spellbook.spell.Spell
import kotlinx.serialization.Serializable

@Serializable
data class SpellGroup(val spells: List<Spell> = emptyList(), val subgroups: Map<String, SpellGroup> = emptyMap()) {
    fun isEmpty() = spells.isEmpty() && subgroups.isEmpty()
}