package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.utils.SerializableImmutableSet
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.serialization.Serializable


@Serializable
data class SpellReference(val name: String)

/**
 * A null [rank] means to use the character's max rank (level / 2)
 */
@Serializable
data class SpellAtRank(val spell: SpellReference, val rank: Int?)

/**
 * [overloading] is how many spells can be put in this slot
 */
@Serializable
data class SpellSlot(
    val maxRank: Int,
    val minRank: Int,
    val lists: SerializableImmutableSet<SpellList>,
    val overloading: Int = 1,
    val spells: SerializableImmutableSet<SpellAtRank> = persistentSetOf()
) {
    init {
        require(spells.size <= overloading) { "Slot can only contain $overloading spells" }
    }

    val remainingSpells get() = overloading - spells.size

    constructor(rank: Int, lists: ImmutableSet<SpellList>) : this(
        rank,
        rank,
        lists,
    )

    constructor(lists: ImmutableSet<SpellList>) : this(
        0,
        10,
        lists,
    )
}