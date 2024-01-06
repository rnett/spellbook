package com.rnett.spellbook.extractor

import kotlinx.serialization.Serializable

// The overall spell class

/**
 * A Pathfinder 2E spell.
 *
 * @property metadata The metadata describing the spell
 * @property effects The effects the spell has when cast
 * @property heightening The way the spell is heightened.
 */
@Serializable
data class Spell(
    val metadata: SpellMetadata,
    val effects: SpellEffects,
    val heightening: Heightening
)