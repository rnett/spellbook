package com.rnett.spellbook

import com.rnett.spellbook.spell.SpellList

operator fun SpellList.Companion.invoke(name: String): SpellList {
    return SpellList.values().singleOrNull { it.name == name.toLowerCase().capitalize() } ?: SpellList.Other
}