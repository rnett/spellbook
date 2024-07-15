package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.utils.SerializableImmutableList
import com.rnett.spellbook.utils.SerializableImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


@Serializable
enum class SpellcastingArchetypeFeat {
    Dedication, Basic, Expert, Master;
}

@Serializable
sealed interface SpellcastingAmount {
    @Serializable
    data class Full(val slotsPerRank: Int, val tenthSlot: Boolean, val extraTenthSlot: Boolean) : SpellcastingAmount

    @Serializable
    data class FullArchetype(
        val breadthTakenAt: Int?,
        val featsTakenAt: SerializableImmutableMap<SpellcastingArchetypeFeat, Int>
    ) :
        SpellcastingAmount

    @Serializable
    data class Bounded(val slotsPerRank: Int) : SpellcastingAmount

    @Serializable
    data class BoundedArchetype(val featsTakenAt: SerializableImmutableMap<SpellcastingArchetypeFeat, Int>) :
        SpellcastingAmount
}

@Serializable
@JvmInline
value class RankMap<T>(val perRank: SerializableImmutableMap<Int?, T> = persistentMapOf()) : Map<Int?, T> by perRank {
    companion object {
        val ALL_RANKS = null
        val CANTRIPS = 0
        val MAX_RANK = 10
    }

    init {
        if (perRank.isNotEmpty()) {
            require(perRank.keys.minOf { it ?: CANTRIPS } >= CANTRIPS) { "Minimum rank is $CANTRIPS" }
            require(perRank.keys.maxOf { it ?: MAX_RANK } <= MAX_RANK) { "Maximum rank is $MAX_RANK" }
        }
    }

    val allRanks get() = this[ALL_RANKS]
}

@Serializable
@JvmInline
value class LevelMap<T>(val atLevel: SerializableImmutableMap<Int?, T> = persistentMapOf()) : Map<Int?, T> by atLevel {
    companion object {
        val ALL_LEVELS = null
        val MIN_LEVEL = 1
        val MAX_LEVEL = 20
    }

    init {
        if (atLevel.isNotEmpty()) {
            require(atLevel.keys.minOf { it ?: MIN_LEVEL } >= MIN_LEVEL) { "Minimum level is $MIN_LEVEL" }
            require(atLevel.keys.maxOf { it ?: MAX_LEVEL } <= MAX_LEVEL) { "Maximum level is $MAX_LEVEL" }
        }
    }

    val allLevels get() = this[ALL_LEVELS]
}

typealias LeveledExtraSpells = LevelMap<RankMap<Int>>

@Serializable
sealed interface SpellcastingSpec {
    fun toSpellcasting(name: String): Spellcasting

    @Serializable
    data class Prepared(val amount: SpellcastingAmount, val list: SpellList, val extraSlots: LeveledExtraSpells) :
        SpellcastingSpec {
        override fun toSpellcasting(name: String): Spellcasting = Spellcasting.Prepared(name, this)
    }

    @Serializable
    data class Flexible(val amount: SpellcastingAmount, val list: SpellList, val extraSlots: LeveledExtraSpells) :
        SpellcastingSpec {
        override fun toSpellcasting(name: String): Spellcasting = Spellcasting.Flexible(name, this)
    }

    /**
     * For [extraSignatures], the key is the max rank of the signature spell.
     * [hasSupplementaryKnown] is whether the caster also has a list of known spells where one can be added to the repertoire or made signature.
     */
    @Serializable
    data class Spontaneous(
        val amount: SpellcastingAmount,
        val list: SpellList,
        val expandedSignatures: Int?,
        val hasSupplementaryKnown: Int?,
        val extraSlots: LeveledExtraSpells,
        val extraRepertoire: LeveledExtraSpells,
        val extraSignatures: LeveledExtraSpells,
        val fromOtherLists: LevelMap<Int>
    ) : SpellcastingSpec {
        override fun toSpellcasting(name: String): Spellcasting = Spellcasting.Spontaneous(name, this)
    }

    @Serializable
    data class Captivator(
        val spellcastingFeats: SpellcastingArchetypeFeat,
        val intensity: Boolean,
        val heightened: Int
    ) : SpellcastingSpec {
        override fun toSpellcasting(name: String): Spellcasting = Spellcasting.Captivator(name, this)
    }

    @Serializable
    data class InnateSlots(
        val slots: RankMap<Int>,
    ) : SpellcastingSpec {
        override fun toSpellcasting(name: String): Spellcasting = Spellcasting.InnateSlots(name, this)
    }
}

@Serializable
sealed interface Spellcasting {
    val name: String

    @Serializable
    data class Focus(
        val spells: SerializableImmutableList<SpellReference>,
        override val name: String = "Focus"
    ) : Spellcasting

    @Serializable
    data class Spontaneous(
        override val name: String,
        val spec: SpellcastingSpec.Spontaneous
    ) : Spellcasting

    @Serializable
    data class Prepared(
        override val name: String,
        val spec: SpellcastingSpec.Prepared
    ) : Spellcasting

    @Serializable
    data class Flexible(
        override val name: String,
        val spec: SpellcastingSpec.Flexible
    ) : Spellcasting

    @Serializable
    data class Captivator(
        override val name: String,
        val spec: SpellcastingSpec.Captivator
    ) : Spellcasting

    @Serializable
    data class InnateSlots(
        override val name: String,
        val spec: SpellcastingSpec.InnateSlots
    ) : Spellcasting

    //TODO Innate other

    @Serializable
    data class Items(val items: SerializableImmutableList<SpellcastingItem>, override val name: String = "Items") :
        Spellcasting

    @Serializable
    data class Stave(
        override val name: String,
        val ranks: SerializableImmutableList<SerializableImmutableList<SpellSlot>>
    ) : Spellcasting
}