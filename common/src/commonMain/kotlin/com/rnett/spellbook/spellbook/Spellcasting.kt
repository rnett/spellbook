package com.rnett.spellbook.spellbook

import com.rnett.spellbook.spell.SpellList
import kotlinx.serialization.Serializable

@Serializable
data class Spellbook(val spellcastings: Map<String, Spellcasting<*>>)

enum class SpellbookType {
    Prepared, Spontaneous;
}

@Serializable
sealed class SpellLevel {
    @Serializable
    data class Prepared(val known: List<SpellSlot>, val numPrepared: Int, val prepared: List<Int>) : SpellLevel() {
        constructor(list: SpellList, cantrips: Boolean, size: Int) : this(List(size) { SpellSlot(list, cantrips) }, size, listOf())
    }

    @Serializable
    data class Spontaneous(val numSignatures: Int, val signatures: Set<Int>, val slots: List<SpellSlot>) : SpellLevel() {
        constructor(list: SpellList, cantrips: Boolean, size: Int, signatures: Int) : this(signatures,
            setOf(),
            List(size) { SpellSlot(list, cantrips) })

        val numSlots get() = slots.size
    }
}

fun <T> List<T>.withReplace(i: Int, new: T): List<T> {
    val m = toMutableList()
    m[i] = new
    return m.toList()
}

@Serializable
sealed class Spellcasting<out L : SpellLevel>(val type: SpellbookType) {
    abstract val cantrips: L
    abstract val levels: List<L>

    @OptIn(ExperimentalStdlibApi::class)
    val allSpells by lazy {
        buildList {
            add(cantrips)
            addAll(levels)
        }
    }

    val maxLevel: Int get() = levels.size

    operator fun get(level: Int) = if (level == 0) cantrips else levels[level - 1]

    @Serializable
    data class Prepared(override val cantrips: SpellLevel.Prepared, override val levels: List<SpellLevel.Prepared>) :
        Spellcasting<SpellLevel.Prepared>(SpellbookType.Prepared) {
        constructor(list: SpellList, levelSizes: List<Int>) : this(
            SpellLevel.Prepared(list, true, levelSizes[0]),
            levelSizes.drop(1).map { SpellLevel.Prepared(list, false, it) }
        )
    }

    @Serializable
    data class Spontaneous(override val cantrips: SpellLevel.Spontaneous, override val levels: List<SpellLevel.Spontaneous>) :
        Spellcasting<SpellLevel.Spontaneous>(SpellbookType.Spontaneous) {
        constructor(list: SpellList, levelSizes: List<Int>, signatures: List<Int>) : this(
            SpellLevel.Spontaneous(list, true, levelSizes[0], 0),
            levelSizes.drop(1).zip(signatures + List(signatures.size - levelSizes.lastIndex) { 0 })
                .map { (size, sigs) -> SpellLevel.Spontaneous(list, false, size, sigs) }
        )
    }

    companion object {
        fun fullCaster(type: SpellbookType, list: SpellList, numSlots: Int): Spellcasting<SpellLevel> {
            val slots = listOf(5) + List(9) { numSlots } + 1
            return when (type) {
                SpellbookType.Prepared -> Prepared(list, slots)
                SpellbookType.Spontaneous -> Spontaneous(list, slots, List(10) { 1 })
            }
        }

        fun archetypeCaster(type: SpellbookType, list: SpellList, bredth: Boolean = true): Spellcasting<SpellLevel> {
            val slots = listOf(2) + List(6) { if (bredth) 2 else 1 } + listOf(1, 1)
            return when (type) {
                SpellbookType.Prepared -> Prepared(list, slots)
                SpellbookType.Spontaneous -> Spontaneous(list, slots, listOf(1, 1, 1))
            }
        }
    }
}

fun <T : Spellcasting<L>, L : SpellLevel> T.withLevel(level: Int, newValue: L): T = when (this) {
    is Spellcasting.Prepared -> {
        if (level == 0)
            copy(cantrips = newValue as SpellLevel.Prepared)
        else
            copy(levels = levels.withReplace(level, newValue as SpellLevel.Prepared))
    }
    is Spellcasting.Spontaneous -> {
        if (level == 0)
            copy(cantrips = newValue as SpellLevel.Spontaneous)
        else
            copy(levels = levels.withReplace(level, newValue as SpellLevel.Spontaneous))
    }
    else -> error("Unknown spellbook type $this")
} as T