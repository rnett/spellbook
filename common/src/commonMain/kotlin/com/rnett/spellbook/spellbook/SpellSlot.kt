package com.rnett.spellbook.spellbook

import com.rnett.spellbook.filter.*
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName

@JvmName("forNullableSLot")
fun SpellFilter.forSlot(slot: LevelSlot?) = if (slot != null) forSlot(slot) else this

fun SpellFilter.forSlot(slot: LevelSlot) = copy(
    lists = singleOrClauseFilter(slot.slot.lists),
    isFocus = SpellList.Focus in slot.slot.lists,
    level = LevelFilter(slot.level),
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
data class LevelSlot(val level: Int, val slot: SpellSlot)

@Serializable
data class SpellSlot(val lists: Set<SpellList>, val type: SpellType, val spell: Spell? = null)