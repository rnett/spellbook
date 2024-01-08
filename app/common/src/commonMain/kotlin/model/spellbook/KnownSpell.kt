package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.filter.LevelFilter
import com.rnett.spellbook.model.filter.SpellFilter
import com.rnett.spellbook.model.filter.defaultOrFilter
import com.rnett.spellbook.model.filter.singleOrClauseFilter
import com.rnett.spellbook.model.spell.Spell
import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.model.spell.SpellType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName
import kotlin.math.min

@JvmName("forNullableSLot")
fun SpellFilter.forSlot(slot: SpellSlotSpec?) = if (slot != null) forSlot(slot) else this

fun SpellFilter.forSlot(slot: SpellSlotSpec) = slot.addToFilter(this)

fun SpellFilter.withoutAnySlot() = copy(
    lists = defaultOrFilter(),
    level = LevelFilter()
)

fun acceptsSpell(maxLevel: Int, isCantrip: Boolean, type: SpellType, lists: Set<SpellList>, spell: Spell): Boolean {
    if (isCantrip != spell.isCantrip) return false
    if (spell.level > maxLevel) return false
    if (type != spell.type) return false

    return spell.lists.any { it in lists }
}

@Serializable
data class SpellSlotSpec(val maxLevel: Int, val slot: KnownSpell, val startAtMax: Boolean = true) {
    fun accepts(spell: Spell): Boolean {
        return slot.accepts(maxLevel, spell)
    }

    fun addToFilter(filter: SpellFilter): SpellFilter {
        return filter.copy(
            lists = singleOrClauseFilter(slot.lists),
            isFocus = SpellList.Focus in slot.lists,
            isCantrip = slot.isCantrip,
            level = if (startAtMax) LevelFilter(maxLevel) else LevelFilter(
                min(filter.level.min, maxLevel),
                min(filter.level.max, maxLevel)
            )
        )
    }
}

@Serializable
data class KnownSpell(
    val lists: Set<SpellList>,
    val type: SpellType,
    val isCantrip: Boolean,
    val spell: Spell? = null
) {
    fun accepts(maxLevel: Int, spell: Spell) = acceptsSpell(maxLevel, isCantrip, type, lists, spell)
}