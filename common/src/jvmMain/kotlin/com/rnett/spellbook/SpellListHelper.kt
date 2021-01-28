package com.rnett.spellbook

operator fun SpellList.Companion.invoke(name: String): SpellList {
    return SpellList.values().singleOrNull { it.name == name.toLowerCase().capitalize() } ?: SpellList.Other
}