package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellDef
import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.model.spell.SpellRef
import com.rnett.spellbook.utils.SerializableImmutableSet
import kotlinx.serialization.Serializable

@Serializable
sealed interface KnownSpellSlot {
    val selectSpell: Boolean
    fun isAllowed(spell: SpellDef): Boolean
    val appliesToAllRanks: Boolean

    @Serializable
    data class Learned(
        val spellLists: SerializableImmutableSet<SpellList>,
        val additionalSpells: SerializableImmutableSet<SpellRef>
    ) : KnownSpellSlot {
        override val selectSpell: Boolean = true

        override fun isAllowed(spell: SpellDef): Boolean {
            return spell.ref in additionalSpells || spell.lists.intersect(spellLists).isNotEmpty()
        }

        override val appliesToAllRanks: Boolean = false
    }

    @Serializable
    data class Spontaneous(
        val signature: Boolean,
        val spellLists: SerializableImmutableSet<SpellList>,
        val additionalSpells: SerializableImmutableSet<SpellRef>
    ) : KnownSpellSlot {
        override val selectSpell: Boolean = true

        override fun isAllowed(spell: SpellDef): Boolean {
            return spell.ref in additionalSpells || spell.lists.intersect(spellLists).isNotEmpty()
        }

        override val appliesToAllRanks: Boolean = signature
    }

    @Serializable
    data class GrantedSpontaneous(val spell: SpellRef) : KnownSpellSlot {
        override val selectSpell: Boolean = false

        override fun isAllowed(spell: SpellDef): Boolean {
            return spell.ref == this.spell
        }

        override val appliesToAllRanks: Boolean = false
    }

    @Serializable
    data class GrantedLearned(val spell: SpellRef) : KnownSpellSlot {
        override val selectSpell: Boolean = false

        override fun isAllowed(spell: SpellDef): Boolean {
            return spell.ref == this.spell
        }

        override val appliesToAllRanks: Boolean = false
    }
}

fun KnownSpellSlot.copy() = when (this) {
    is KnownSpellSlot.GrantedLearned -> copy()
    is KnownSpellSlot.GrantedSpontaneous -> copy()
    is KnownSpellSlot.Learned -> copy()
    is KnownSpellSlot.Spontaneous -> copy()
}