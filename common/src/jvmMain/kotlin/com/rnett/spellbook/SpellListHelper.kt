package com.rnett.spellbook

import com.rnett.spellbook.spell.SpellList
import java.util.Locale

operator fun SpellList.Companion.invoke(name: String): SpellList {
    return SpellList.values().singleOrNull { it.name == name.lowercase(Locale.getDefault()).capitalize() }
        ?: SpellList.Other
}