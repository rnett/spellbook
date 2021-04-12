package com.rnett.spellbook

import com.rnett.spellbook.filter.SpellFilter

interface SavedSearchRepository {
    val all: List<Pair<String, SpellFilter>>
    operator fun get(idx: Int): Pair<String, SpellFilter> = all[idx]

    fun remove(idx: Int)

    fun rename(idx: Int, new: String)

    fun add(name: String, filter: SpellFilter): Int

    operator fun set(idx: Int, value: SpellFilter)
}