package com.rnett.spellbook.extractor.advanced.actions

import kotlinx.serialization.Serializable

// Types describing the actions used to cast a spell

/**
 * The actions used to cast a spell
 *
 * @property actions The actions used during casting
 * @property castActionTypes The types of the actions used
 */
@Serializable
data class SpellActions(val actions: Actions, val castActionTypes: List<CastActionType>)

/**
 * The types of actions used to cast the spell
 */
@Serializable
enum class CastActionType {
    Material, Somatic, Verbal, Focus;
}

/**
 * The actions used to cast a spell.
 * Only one of [variable] [constant] should be non-null.
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