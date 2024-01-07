package com.rnett.spellbook.extractor.advanced.damage

import com.rnett.spellbook.extractor.advanced.Check
import com.rnett.spellbook.extractor.advanced.CheckDegreesOfSuccess
import com.rnett.spellbook.extractor.advanced.Heightening
import kotlinx.serialization.Serializable

/*
Types used for defining the damage done by a spell

Note that damage dice shouldn't be changed when multiplying by a number for the degree of success - use the `multiple` field instead.
This is important because those multiples affect the final damage done after rolling the damage dice, not the number of dice rolled.
 */

/**
 * The damage done by a spell, including how heightening affects it.
 * This object is only concerned with the damage done, and ignores any other effects (e.g. conditions) of the spell.
 *
 * @property checks The checks made when doing damage. Some spells may have multiple checks, e.g. a spell attack and a basic save.
 * @property damageSources The damage sources of the spell. This should include all possible damage sources, including those that only apply when heightened.
 * Note that when making multiple attacks against different targets, only use one damage source - the targeting of the spell determines what it applies to.
 * @property baseDamage The damage done at the base level of the spell. If there are multiple checks, the [CheckDegreesOfSuccess] will be nested.
 * @property heightenedDamage The effects of heightening on the damage done
 * @property damageDoneOnSustain If the spell is sustained, whether the damage is done again when sustaining the spell
 */
@Serializable
data class SpellDamage(
    val checks: List<Check>,
    val damageSources: Map<DamageSourceId, DamageSource>,
    val baseDamage: CheckDegreesOfSuccess<AppliedDamages>,
    val heightenedDamage: Heightening<AppliedDamages>,
    val damageDoneOnSustain: Boolean = false
)

/**
 * The ID of a damage source.
 * Each damage source should have its own ID.
 * This can be referenced when modifying the damage done, for example doubling it on a crit, or adding more damage when heightening.
 *
 * Should generally start at 0 and increase for each new source of damage.
 */
@Serializable
@JvmInline
value class DamageSourceId(val id: Int)

/**
 * Some damage applied by a spell.
 *
 * @property possibleTypes The possible damage types, e.g. fire or acid. Most damage only has one possible type, but some damage allows you to choose which type
 * @property persistent Whether the damage is persistent damage
 */
@Serializable
data class DamageSource(val possibleTypes: List<String>, val persistent: Boolean = false, val splash: Boolean = false)

/**
 * Some damage applied by a spell.
 *
 * Remember that for basic saves (and most other saves) and normal damage, the multiple are usually 0 on critical success, 0.5 on success, 1 on failure, and 2 on critical failure.
 * For spell attacks, the multiple should be 1 on success and 2 on critical success.
 * If there are multiple checks done, the last one is usually the one that determines the multiple.
 * Spells may also have custom multiples.
 *
 * @property source The [DamageSourceId] of the damage source this damage corresponds to
 * @property dice The base damage dice of the damage, e.g. 3d6.  Remember not to double or halve the damage dice when doubling or halving the total damage.
 * @property multiple Any multipliers (e.g. doubling or halving) applied to the damage. Typically used as a result of the degree of success.
 */
@Serializable
data class Damage(val source: DamageSourceId, val dice: String, val multiple: Double = 1.0)

/**
 * Multiple applied damage, linked to their sources.
 * Each damage source key in [damages] should correspond to a damage source ID defined elsewhere.
 * Any damage with a multiple of 0 can be omitted from the [damages] map, but this object should still be present.
 *
 * @property damages The actual damage done for each damage source.
 */
@Serializable
data class AppliedDamages(val damages: Map<DamageSourceId, Damage>)