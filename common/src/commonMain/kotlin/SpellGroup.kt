package com.rnett.spellbook.group

import com.rnett.spellbook.NamedList
import com.rnett.spellbook.emptyNamedList
import com.rnett.spellbook.spell.Spell
import kotlinx.serialization.Serializable

@Serializable
data class SpellGroup(val spells: List<Spell> = emptyList(), val subgroups: NamedList<SpellGroup> = emptyNamedList()) {
    fun isEmpty() = spells.isEmpty() && subgroups.isEmpty()
}