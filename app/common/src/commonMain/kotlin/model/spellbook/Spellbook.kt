package com.rnett.spellbook.model.spellbook

import com.rnett.spellbook.utils.SerializableImmutableList
import kotlinx.serialization.Serializable

@Serializable
data class Spellbook(
    val name: String,
    val spellcastings: SerializableImmutableList<SpellcastingAndSpells>,
)

