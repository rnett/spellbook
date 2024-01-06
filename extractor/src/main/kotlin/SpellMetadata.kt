package com.rnett.spellbook.extractor

import kotlinx.serialization.Serializable

// Types used for defining the various metadata about a spell

/**
 * The metadata describing a Pathfinder 2E spell.
 *
 * @property name The spell's name
 * @property level The spell's base level
 * @property traditions The spell's traditions
 * @property castActionTypes The types of actions used to cast the spell
 * @property actions The actions used to cast the spell
 * @property access What is required to get access to the spell
 * @property requirements Any requirements for the spell to be cast.
 * @property source The source of the spell (i.e. the book it's from)
 * @property targeting The targeting information about the spell
 * @property traits The spell's traits
 * @property checks The checks required when casting the spell
 * @property damageSources All possible damage sources of the spell, even those that are only applied under some conditions
 */
@Serializable
data class SpellMetadata(
    val name: String,
    val level: Int,
    val traditions: List<Tradition>,
    val castActionTypes: List<CastActionType>,
    val actions: Actions,
    val access: String?,
    val requirements: String?,
    val source: String,
    val targeting: Targeting,
    val traits: List<String>,
    val checks: List<Check>,
    val damageSources: Map<DamageSourceId, DamageSource>
)

/**
 * The targeting information about a spell
 *
 * @property range The spell's range, if it has one
 * @property targets The spell's targets, if it has one
 * @property duration The spell's duration, if it has one
 * @property area The spell's area, if it has one
 */
@Serializable
data class Targeting(
    val range: String? = null,
    val targets: String? = null,
    val duration: String? = null,
    val area: String? = null,
)

/**
 * The types of actions used to cast the spell
 */
@Serializable
enum class CastActionType {
    Material, Somatic, Verbal, Focus;
}

/**
 * The actions used to cast a spell.
 * Only one of [variable], [constant], or [reaction] should be non-null.
 *
 * @property trigger The trigger for the actions, if there is one
 * @property sustained Whether the spell can be sustained
 * @property variable The variable actions used to cast the spell, if it can be cast with variable actions.  Null otherwise.
 * @property constant The actions used to cast the spell, if it is cast with a constant number of actions.  Use 0 for a free action, and negative one for a reaction.
 */
@Serializable
data class Actions(
    val trigger: String?,
    val sustained: Boolean = false,
    val variable: VariableActions? = null,
    val constant: Int? = null
)

@Serializable
data class VariableActions(val min: Int, val max: Int)


/**
 * A spell casting tradition
 */
@Serializable
enum class Tradition {
    Arcane, Primal, Divine, Occult;
}

/**
 * A spell casting tradition
 */
@Serializable
enum class Check {
    SpellAttack, Fortitude, Reflex, Will;
}
