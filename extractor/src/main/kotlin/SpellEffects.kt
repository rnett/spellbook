package com.rnett.spellbook.extractor

import kotlinx.serialization.Serializable

// Types used for defining the effects done by a spell based on its checks' outcomes

/**
 * The effects applied by a spell.
 *
 * @property damage The damage done for each degree of success of the spell.  The damage sources used here must correspond to the ones defined in the spell's metadata's damageSources.
 * @property conditions The conditions applied for each degree of success of the spell.  Note that persistent damage should not be in here, it should go under [damage] instead.
 */
@Serializable
data class SpellEffects(
    val damage: DegreesOfSuccess<AppliedDamages>,
    val conditions: DegreesOfSuccess<AppliedConditions>
)


/**
 * Contains the results of each possible degree of success of some action
 */
@Serializable
data class DegreesOfSuccess<T>(val criticalSuccess: T, val success: T, val failure: T, val criticalFailure: T)
