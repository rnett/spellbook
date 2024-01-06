package com.rnett.spellbook.extractor

import kotlinx.serialization.Serializable

// Types used for defining the damage done by a spell

/**
 * The ID of a damage source.
 * Each damage source should have its own ID.
 * This can be referenced when modifying the damage done, for example doubling it on a crit, or adding more damage when heightning.
 *
 * Should generally start at 0 and increase for each new source of damage.
 */
@Serializable
data class DamageSourceId(val id: Int)

/**
 * Some damage applied by a spell.
 *
 * @property type The damage type, e.g. fire or acid
 * @property persistent Whether the damage is persistent damage
 */
@Serializable
data class DamageSource(val type: String, val persistent: Boolean = false, val splash: Boolean = false)

/**
 * Some damage applied by a spell.
 *
 * @property source The [DamageSourceId] of the damage source this damage corresponds to
 * @property dice The base damage dice of the damage, e.g. 3d6.  Remember not to double or halve the damage dice when doubling or halving the total damage.
 * @property multiple Any multiple that should be applied after the damage is calculated, e.g. halving for a successful save, or doubling on a critical hit.  May be omitted if it is one.
 */
@Serializable
data class Damage(val source: DamageSourceId, val dice: String, val multiple: Double = 1.0)

/**
 * Multiple applied damage, linked to their sources.
 * Each damage source key in [damages] should correspond to a damage source ID defined elsewhere.
 *
 * @property damages The actual damage done for each damage source.
 */
@Serializable
data class AppliedDamages(val damages: Map<DamageSourceId, Damage>)