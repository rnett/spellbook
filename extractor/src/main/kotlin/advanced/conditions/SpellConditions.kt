package com.rnett.spellbook.extractor.advanced.conditions

import com.rnett.spellbook.extractor.advanced.CheckDegreesOfSuccess
import com.rnett.spellbook.extractor.advanced.Heightening
import com.rnett.spellbook.extractor.advanced.damage.AppliedDamages
import com.rnett.spellbook.extractor.basic.Check
import kotlinx.serialization.Serializable

/*
Types used for defining the conditions applied by a spell
Conditions include specific conditions with a value, e.g. `frightened 2`, `sickened`; bonuses and penalties, e.g. `a +2 status bonus to Perception`, `a -1 penalty to Reflex saves`; custom conditions like afflictions, diseases, or curses, e.g. `afflicted with pox at stage 2`
 */

/**
 * The conditions applied by a spell, including how heightening affects them
 *
 * @property checks The checks made when applying conditions
 * @property customConditions Any custom conditions the spell defines
 * @property baseConditions Conditions applied at the base level of a spell
 * @property heightenedConditions The effects of heightening the spell on the conditions it applies
 */
@Serializable
data class SpellConditions(
    val checks: List<Check>,
    val customConditions: List<CustomCondition>,
    val baseConditions: CheckDegreesOfSuccess<AppliedConditions>,
    val heightenedConditions: Heightening<AppliedConditions>
)

/**
 * A custom condition (e.g. diseases, poison, curses, afflictions) defined the spell that has stages.
 * This should only be used for conditions actually defined by the spell, not just conditions it references.
 * For example, a definition entry for "goblin pox" would be a custom condition, but a spell that includes a status penalty to attack roles would not.
 *
 * @property name The name of the condition
 * @property stages The stages (starting at 1)
 * @property maxDuration The maximum duration of the condition, if it has one
 */
@Serializable
data class CustomCondition(
    val name: String,
    val stages: List<CustomConditionStage>,
    val maxDuration: String?
)

/**
 * A state of a custom condition
 *
 * @property duration The duration of the stage, if it has one
 * @property conditions Conditions applied by the stage, if any
 * @property damages Damage applied by the stage, if any
 */
@Serializable
data class CustomConditionStage(
    val duration: String?,
    val conditions: AppliedConditions?,
    val damages: AppliedDamages?
)

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
 * @property name The name of the condition.  May reference a custom condition
 * @property strength The strength of the condition. Will be specified as a number value, e.g. `frightened 2` would have a strength of 2. Entirely unrelated to the duration.
 * Can be the state a condition is afflicted at.
 * @property strengthAdditive Whether the strength should be added to the strength of any already applied instances of this condition. Sometimes used when heightening a spell
 * @property duration The duration of the condition, if it has one.
 * @property durationAdditive Whether the duration should be added to the duration of any already applied instance of this condition. Sometimes used when heightening a spell
 */
@Serializable
data class Condition(
    val name: String?,
    val strength: Int? = null,
    val strengthAdditive: Boolean = false,
    val duration: String? = null,
    val durationAdditive: Boolean = false
)