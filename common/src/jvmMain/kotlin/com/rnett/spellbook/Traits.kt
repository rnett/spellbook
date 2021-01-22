package com.rnett.spellbook

object f

operator fun SpellList.Companion.invoke(name: String): SpellList {
    return SpellList.values().singleOrNull { it.name == name.toLowerCase().capitalize() } ?: SpellList.Other
}

operator fun Trait.Companion.invoke(name: String): Trait {
    val name = name.toLowerCase().capitalize()
    return definedTraits[name] ?: Trait.Other(name)
}