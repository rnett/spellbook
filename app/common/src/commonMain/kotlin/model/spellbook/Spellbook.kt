package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.utils.SerializableImmutableList
import com.rnett.spellbook.utils.SerializableImmutableSet
import com.rnett.spellbook.utils.SerializablePersistentMap
import com.rnett.spellbook.utils.SerializablePersistentSet
import kotlinx.serialization.Serializable

@Serializable
data class Spellbook(
    val name: String,
    val spellcastings: SerializableImmutableList<Spellcasting>
)

//TODO split up creating slots & assigning spells to them (i.e. use an ID for slots to assign)

@Serializable
data class SpellSlotList(
    val numSlots: Int,
    val modifiers: SerializablePersistentMap<Int, SerializablePersistentSet<SlotModifier>>,
    val spells: SerializablePersistentMap<Int, SpellAtRank>
)

@Serializable
sealed class SlotModifier(val exclusive: Boolean = false) {
    @Serializable
    data object Signature : SlotModifier()

    /**
     * @param lists Null means to allow all lists
     */
    @Serializable
    data class AllowLists(val lists: SerializablePersistentSet<SpellList>? = null) : SlotModifier()

    /**
     * Allow putting [additionalSpells] spells in this slot.
     */
    @Serializable
    data class Overloaded(val additionalSpells: Int) : SlotModifier()

    @Serializable
    data class LimitToGroup(val group: String) : SlotModifier(true)
}

@Serializable
sealed interface Spellcasting {
    val name: String

    data class Focus(
        val spells: SerializableImmutableList<SpellReference>,
        override val name: String = "Focus"
    ) : Spellcasting

    data class Spontaneous(
        override val name: String,
        val ranks: SerializableImmutableList<SpellSlotList>,
    ) : Spellcasting

    data class Prepared(
        override val name: String,
        val defaultLists: SerializableImmutableSet<SpellList>,
        val ranks: SerializableImmutableList<SpellSlotList>
    ) : Spellcasting

    data class Items(val items: SerializableImmutableList<SpellcastingItem>, override val name: String = "Items") :
        Spellcasting

    data class Stave(
        override val name: String,
        val ranks: SerializableImmutableList<SerializableImmutableList<SpellSlot>>
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