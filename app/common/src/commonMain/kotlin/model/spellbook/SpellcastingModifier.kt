package com.rnett.spellbook.model.spellbook

import kotlinx.serialization.Serializable

@Serializable
sealed interface SpellcastingModifier<T> {
    val name: String
    val description: String
    fun appliesTo(spellcasting: Spellcasting, rank: SpellcastingRank, value: T): Boolean
    fun modify(spellcasting: Spellcasting, rank: SpellcastingRank, value: T): T
}

interface SpellSlotModifier : SpellcastingModifier<SpellSlot>
interface KnownSpellModifier : SpellcastingModifier<KnownSpellSlot>