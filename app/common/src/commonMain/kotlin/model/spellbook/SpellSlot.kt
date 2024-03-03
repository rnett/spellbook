package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.utils.SerializableImmutableSet
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.serialization.Serializable


@Serializable
data class SpellReference(val name: String)

/**
 * A null [rank] means to use the character's max rank (level / 2)
 */
@Serializable
data class SpellAtRank(val spell: SpellReference, val rank: Int?)

@Serializable
data class SpellSlot(
    val maxRank: Int,
    val minRank: Int,
    val lists: SerializableImmutableSet<SpellList>,
    val spell: SpellAtRank? = null
) {
    constructor(rank: Int, lists: ImmutableSet<SpellList>, spell: SpellAtRank? = null) : this(
        rank,
        rank,
        lists,
        spell
    )
}

data class SettableSpellSlot(val slot: SpellSlot, val set: (SpellReference) -> Unit)