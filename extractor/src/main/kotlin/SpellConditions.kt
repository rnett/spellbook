package com.rnett.spellbook.extractor

import kotlinx.serialization.Serializable

// Types used for defining the conditions applied by a spell

/**
 * The conditions applied by a spell
 *
 * @property conditions The conditions
 */
@Serializable
data class AppliedConditions(val conditions: List<Condition>)

/**
 * A condition applied by a spell.
 *
 * @property name The name of the condition
 * @property strength The strength of the condition. Will be specified as a number value, e.g. `frightened 2` would have a strength of 2. Entirely unrelated to the duration
 * @property strengthAdditive Whether the strength should be added to the strength of any already applied instances of this condition. Sometimes used when heightening a spell
 * @property duration The duration of the condition, if it has one.
 * @property durationAdditive Whether the duration should be added to the duration of any already applied instance of this condition. Sometimes used when heightening a spell
 */
@Serializable
data class Condition(
    val name: String,
    val strength: Int? = null,
    val strengthAdditive: Boolean = false,
    val duration: String? = null,
    val durationAdditive: Boolean = false
)