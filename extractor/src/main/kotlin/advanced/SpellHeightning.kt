package com.rnett.spellbook.extractor.advanced

import kotlinx.serialization.Serializable

// Types used for defining how a spell is affected by heightening

/**
 * How a spell is heightened.  May either be incremental, e.g. "Heighten +2", or on specific levels, e.g. "Heighten 3, Heighten 7".
 * One of [incremental] or [specific] should be specified, and the other should be omitted or null.
 *
 * @property incremental The incremental heightening of a spell, if it is used.
 * @property specific The specific heightening of a spell, if it is used
 */
@Serializable
data class Heightening<H>(
    val incremental: IncrementalHeighten<H>? = null,
    val specific: SpecificHeighten<H>? = null
)

/**
 * Heightening every N levels, e.g. "Heighten +2"
 *
 * @property increment How often new effects are applied.  For example, for a "Heighten +2", this is 2.
 * @property effects The new effects that are applied each increment
 */
@Serializable
data class IncrementalHeighten<H>(val increment: Int, val effects: H)

/**
 * Heightening for specific levels, e.g. "Heighten 3, Heighten 7"
 *
 * @property effects The new effects, for each heightening level defined
 */
@Serializable
data class SpecificHeighten<H>(val effects: Map<Int, H>)