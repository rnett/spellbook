package com.rnett.spellbook.model.spell

interface SpellDefLookup {
    fun getSpellDef(ref: SpellRef): SpellDef
}