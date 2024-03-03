package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellList
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet


data class Spellbook(
    val name: String,
    val spellcastings: ImmutableList<Spellcasting>
)

sealed interface Spellcasting {
    val name: String

    data class Focus(
        val slots: ImmutableList<SpellSlot>,
        val maxFocusPoints: Int,
        override val name: String = "Focus"
    ) : Spellcasting

    data class Spontaneous(
        override val name: String,
        val defaultLists: ImmutableSet<SpellList>,
        val levels: ImmutableList<ImmutableList<SpellSlot>>,
    ) : Spellcasting

    data class Prepared(
        override val name: String,
        val defaultLists: ImmutableSet<SpellList>,
        val levels: ImmutableList<ImmutableList<SpellSlot>>,
        val known: ImmutableSet<SpellReference>,
    ) : Spellcasting

    data class Items(val items: ImmutableList<SpellcastingItem>, override val name: String = "Items") : Spellcasting
    data class Stave(override val name: String, val levels: ImmutableList<ImmutableList<SpellSlot>>) : Spellcasting
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