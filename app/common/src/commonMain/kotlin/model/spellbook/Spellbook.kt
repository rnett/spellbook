package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet


data class Spellbook(
    val name: String,
    val spellcastings: ImmutableList<Spellcasting>
)

sealed interface Spellcasting {
    data class Focus(val slots: ImmutableList<SpellSlot>, val maxFocusPoints: Int) : Spellcasting
    data class Spontaneous(
        val defaultLists: ImmutableSet<SpellList>,
        val levels: ImmutableList<ImmutableList<SpellSlot>>
    ) : Spellcasting

    data class Prepared(
        val defaultLists: ImmutableSet<SpellList>,
        val levels: ImmutableList<ImmutableList<SpellSlot>>,
        val known: ImmutableSet<SpellReference>
    ) : Spellcasting

    data class Items(val items: ImmutableList<SpellcastingItem>) : Spellcasting
    data class Stave(val levels: ImmutableList<ImmutableList<SpellSlot>>) : Spellcasting
}

enum class SpellcastingRecharge {
    SINGLE_USE, DAY, HOUR;
}

sealed class SpellcastingItem {
    abstract val spell: SpellAtRank
    abstract val recharge: SpellcastingRecharge

    data class Scroll(override val spell: SpellAtRank) : SpellcastingItem() {
        override val recharge: SpellcastingRecharge = SpellcastingRecharge.SINGLE_USE
    }

    data class Wand(override val spell: SpellAtRank) : SpellcastingItem() {
        override val recharge: SpellcastingRecharge = SpellcastingRecharge.DAY
    }

    data class Custom(override val spell: SpellAtRank, override val recharge: SpellcastingRecharge) : SpellcastingItem()
}