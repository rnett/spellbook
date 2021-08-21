package com.rnett.spellbook.spellbook

import com.rnett.spellbook.filter.LevelFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.filter.defaultAndFilter
import com.rnett.spellbook.filter.defaultOrFilter
import com.rnett.spellbook.filter.singleOrClauseFilter
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName
import kotlin.math.min

@JvmName("forNullableSLot")
fun SpellFilter.forSlot(slot: LevelKnownSpell?) = if (slot != null) forSlot(slot) else this

fun SpellFilter.forSlot(slot: LevelKnownSpell, strictLevel: Boolean = false) = copy(
    lists = singleOrClauseFilter(slot.slot.lists),
    isFocus = SpellList.Focus in slot.slot.lists,
    level = if (strictLevel) LevelFilter(slot.level) else LevelFilter(
        min(level.min, slot.level),
        min(level.max, slot.level)
    ),
    types = singleOrClauseFilter(setOf(slot.slot.type))
)

fun SpellFilter.withSlotSettingsFrom(other: SpellFilter) = copy(
    lists = other.lists,
    isFocus = other.isFocus,
    level = other.level,
    types = other.types
)

fun SpellFilter.withoutAnySlot() = copy(
    lists = defaultOrFilter(),
    isFocus = false,
    level = LevelFilter(),
    types = defaultAndFilter()
)

@Serializable
data class LevelKnownSpell(val level: Int, val slot: KnownSpell)

@Serializable
data class KnownSpell(val lists: Set<SpellList>, val type: SpellType, val spell: Spell? = null)