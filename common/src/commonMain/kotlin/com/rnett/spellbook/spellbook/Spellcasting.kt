package com.rnett.spellbook.spellbook

import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import kotlinx.serialization.Serializable

@Serializable
data class Spellbook(val spellcastings: Map<String, Spellcasting<*>>)

enum class SpellbookType {
    Prepared, Spontaneous;
}

@Serializable
sealed class SpellLevel {

    abstract fun withKnown(i: Int, spell: Spell): SpellLevel

    @Serializable
    data class Prepared(val known: List<KnownSpell>, val maxPrepared: Int, val prepared: List<Int>) : SpellLevel() {
        companion object {
            fun empty(slots: Int, lists: Set<SpellList>, type: SpellType) = Prepared(
                List(slots) { KnownSpell(lists, type) },
                slots,
                emptyList()
            )
        }

        override fun withKnown(i: Int, spell: Spell): Prepared =
            copy(known = known.withReplace(i) { it.copy(spell = spell) })
    }

    @Serializable
    data class Spontaneous(
        val maxSignatures: Int,
        val signatures: Set<Int>,
        val known: List<KnownSpell>,
        val numSlots: Int
    ) :
        SpellLevel() {

        val numKnown get() = known.size

        override fun withKnown(i: Int, spell: Spell): Spontaneous =
            copy(known = known.withReplace(i) { it.copy(spell = spell) })

        companion object {
            fun empty(slots: Int, signatures: Int, lists: Set<SpellList>, type: SpellType) = Spontaneous(
                signatures,
                emptySet(),
                List(slots) { KnownSpell(lists, type) },
                slots
            )
        }
    }
}

fun <T> List<T>.withReplace(i: Int, new: T): List<T> {
    val m = toMutableList()
    m[i] = new
    return m.toList()
}

fun <T> List<T>.withReplace(i: Int, new: (T) -> T): List<T> {
    val m = toMutableList()
    m[i] = new(m[i])
    return m.toList()
}

@Serializable
sealed class Spellcasting<out L : SpellLevel>(val type: SpellbookType) {
    abstract val cantrips: L
    abstract val levels: List<L>
    abstract val defaultLists: Set<SpellList>

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
    data class Prepared(
        override val cantrips: SpellLevel.Prepared,
        override val levels: List<SpellLevel.Prepared>,
        override val defaultLists: Set<SpellList>
    ) : Spellcasting<SpellLevel.Prepared>(SpellbookType.Prepared)

    @Serializable
    data class Spontaneous(
        override val cantrips: SpellLevel.Spontaneous,
        override val levels: List<SpellLevel.Spontaneous>,
        override val defaultLists: Set<SpellList>
    ) : Spellcasting<SpellLevel.Spontaneous>(SpellbookType.Spontaneous)

    companion object {
        fun fullCaster(
            type: SpellbookType,
            lists: Set<SpellList>,
            numSlots: Int,
            numCantrips: Int = 5,
            numLevel10s: Int = 1
        ): Spellcasting<SpellLevel> {
            return when (type) {
                SpellbookType.Prepared -> Prepared(
                    SpellLevel.Prepared.empty(numCantrips, lists, SpellType.Cantrip),
                    List(9) {
                        SpellLevel.Prepared.empty(numSlots, lists, SpellType.Spell)
                    } + SpellLevel.Prepared.empty(numLevel10s, lists, SpellType.Spell),
                    lists
                )
                SpellbookType.Spontaneous -> Spontaneous(
                    SpellLevel.Spontaneous.empty(numCantrips, 0, lists, SpellType.Cantrip),
                    List(9) {
                        SpellLevel.Spontaneous.empty(numSlots, 1, lists, SpellType.Spell)
                    } + SpellLevel.Spontaneous.empty(numLevel10s, 1, lists, SpellType.Spell),
                    lists
                )
            }
        }

        fun archetypeCaster(
            type: SpellbookType,
            lists: Set<SpellList>,
            bredth: Boolean = true
        ): Spellcasting<SpellLevel> {
            val slots = List(6) { if (bredth) 2 else 1 } + listOf(1, 1)
            return when (type) {
                SpellbookType.Prepared -> Prepared(
                    SpellLevel.Prepared.empty(2, lists, SpellType.Cantrip),
                    slots.map { SpellLevel.Prepared.empty(it, lists, SpellType.Spell) },
                    lists
                )
                SpellbookType.Spontaneous -> Spontaneous(
                    SpellLevel.Spontaneous.empty(2, 0, lists, SpellType.Cantrip),
                    slots.map { SpellLevel.Spontaneous.empty(it, 0, lists, SpellType.Spell) },
                    lists
                )
            }
        }
    }
}

fun <T : Spellcasting<L>, L : SpellLevel> T.withLevel(level: Int, newValue: L): T = when (this) {
    is Spellcasting.Prepared -> {
        if (level == 0)
            copy(cantrips = newValue as SpellLevel.Prepared)
        else
            copy(levels = levels.withReplace(level - 1, newValue as SpellLevel.Prepared))
    }
    is Spellcasting.Spontaneous -> {
        if (level == 0)
            copy(cantrips = (newValue as SpellLevel.Spontaneous).copy(0, emptySet()))
        else
            copy(levels = levels.withReplace(level - 1, newValue as SpellLevel.Spontaneous))
    }
    else -> error("Unknown spellbook type $this")
} as T