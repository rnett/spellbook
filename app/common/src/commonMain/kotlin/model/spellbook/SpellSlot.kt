package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.model.spell.SpellDef
import com.rnett.spellbook.model.spell.SpellRef
import com.rnett.spellbook.utils.SerializableImmutableSet
import kotlinx.serialization.Serializable

@Serializable
data class SpellSlot(val def: SpellSlotDef, val modifiers: SpellSlotModifiers) {
    fun requireKnown(ref: SpellRef): Boolean {
        return def.requireKnown && ref !in modifiers.alsoCanCast
    }
}

@Serializable
sealed interface SpellSlotDef {
    val selectSpells: Int
    val requireKnown: Boolean
    fun canUseKnown(known: KnownSpellSlot): Boolean

    fun canSelectSpell(spell: SpellDef): Boolean {
        return selectSpells > 0
    }

    @Serializable
    data class Prepared(val limitedTo: SerializableImmutableSet<SpellRef>?, val prepareAdditional: Int) : SpellSlotDef {
        override val selectSpells: Int = prepareAdditional + 1
        override val requireKnown: Boolean = true
        override fun canUseKnown(known: KnownSpellSlot): Boolean =
            known is KnownSpellSlot.Learned || known is KnownSpellSlot.GrantedLearned

        override fun canSelectSpell(spell: SpellDef): Boolean {
            if (limitedTo != null) {
                return spell.ref in limitedTo
            }
            return true
        }
    }

    @Serializable
    object Spontaneous : SpellSlotDef {
        override val selectSpells: Int = 0
        override val requireKnown: Boolean = true
        override fun canUseKnown(known: KnownSpellSlot): Boolean =
            known is KnownSpellSlot.Spontaneous || known is KnownSpellSlot.GrantedSpontaneous
    }

    @Serializable
    object Flexible : SpellSlotDef {
        override val selectSpells: Int = 0
        override val requireKnown: Boolean = true
        override fun canUseKnown(known: KnownSpellSlot): Boolean =
            known is KnownSpellSlot.Learned || known is KnownSpellSlot.GrantedLearned
    }

    @Serializable
    data class Specified(val spells: SerializableImmutableSet<SpellRef>) : SpellSlotDef {
        override val selectSpells: Int = 0
        override val requireKnown: Boolean = false
        override fun canUseKnown(known: KnownSpellSlot): Boolean = false
    }
}

@Serializable
data class SpellSlotModifiers(val rankDelta: Int, val alsoCanCast: SerializableImmutableSet<SpellRef>)