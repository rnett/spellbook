package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.utils.SerializableImmutableList
import com.rnett.spellbook.utils.SerializableImmutableSet
import kotlinx.serialization.Serializable

@Serializable
data class Spellbook(
    val name: String,
    val spellcastings: SerializableImmutableList<Spellcasting>
)

@Serializable
sealed interface Spellcasting {
    val name: String

    data class Focus(
        val slots: SerializableImmutableList<SpellSlot>,
        val maxFocusPoints: Int,
        override val name: String = "Focus"
    ) : Spellcasting

    data class Spontaneous(
        override val name: String,
        val defaultLists: SerializableImmutableSet<SpellList>,
        val levels: SerializableImmutableList<SerializableImmutableList<SpellSlot>>,
    ) : Spellcasting

    data class Prepared(
        override val name: String,
        val defaultLists: SerializableImmutableSet<SpellList>,
        val levels: SerializableImmutableList<SerializableImmutableList<SpellSlot>>,
        val known: SerializableImmutableList<SpellReference>,
    ) : Spellcasting

    data class Items(val items: SerializableImmutableList<SpellcastingItem>, override val name: String = "Items") :
        Spellcasting

    data class Stave(
        override val name: String,
        val levels: SerializableImmutableList<SerializableImmutableList<SpellSlot>>
    ) : Spellcasting
}

@Serializable
enum class SpellcastingRecharge {
    SINGLE_USE, DAY, HOUR;
}

@Serializable
sealed class SpellcastingItem {
    abstract val spell: SpellAtRank
    abstract val recharge: SpellcastingRecharge

    @Serializable
    data class Scroll(override val spell: SpellAtRank) : SpellcastingItem() {
        override val recharge: SpellcastingRecharge = SpellcastingRecharge.SINGLE_USE
    }

    @Serializable
    data class Wand(override val spell: SpellAtRank) : SpellcastingItem() {
        override val recharge: SpellcastingRecharge = SpellcastingRecharge.DAY
    }

    @Serializable
    data class Custom(override val spell: SpellAtRank, override val recharge: SpellcastingRecharge) : SpellcastingItem()
}