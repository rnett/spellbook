package com.rnett.spellbook.extractor.basic

import kotlinx.serialization.Serializable

/**
 * A basic description of a spell. This includes all data about how the spell works, but does not deeply analyze it, so it is mostly unstructured text.
 * Instead, it mostly just splits it into the correct categories.
 * This will later be converted into more structured data, so keep the descriptions as consistent as possible.
 *
 * Each description field should be as self-contained as possible, as they will be converted into structured data later.
 * The only information from the overall spell that will be provided for these later conversions are the name, level, and checks - everything else should be included in the description fields.
 *
 * @property basicMetadata Basic metadata about the spell
 * @property durationDescription A description of the duration of the spell
 * @property targetingDescription A description of the targeting of the spell. This includes the range and area
 * @property actionsDescription A description of the actions used to cast the spell
 * @property damageDescription A description of the damage done by the spell, if any is done
 * @property conditionsDescription A description of the conditions applied by the spell, if any
 */
@Serializable
data class SpellDescription(
    val basicMetadata: BasicMetadata,
    val durationDescription: String,
    val targetingDescription: TargetingDescription,
    val actionsDescription: String,
    val damageDescription: DamageDescription?,
    val conditionsDescription: ConditionsDescription?,
)

/**
 * A description of how a spell is targeted, and how that changes as the spell is heightened
 *
 * @property base The targeting at the base level
 * @property heightening How the targeting changes as the spell is heightened, if it does. Should include the levels of heightening.
 * Should include everything necessary to determine the targeting at a given spell level.
 * Should not include anything about damage or conditions.
 */
@Serializable
data class TargetingDescription(val base: String, val heightening: String?)

/**
 * A description of the damage done by a spell.
 *
 * @property definitions Any definitions that are shared between the base damage and the heightening.
 * @property base The base damage done
 * @property heightening How the damage is heightened. Should include the levels of heightening
 * Should include everything necessary to determine the damage done at a given spell level.
 * Should not include anything about targeting or conditions.
 */
@Serializable
data class DamageDescription(val definitions: String, val base: String, val heightening: String)

/**
 * A description of the conditions applied by a spell
 *
 * @property definitions Any definitions that are used. For example, custom conditions or poisons defined by the spell.
 * @property base The base conditions applied by the spell
 * @property heightening How the conditions change as the spell is heightened, if they do. Should include the levels of heightening.
 * Should include everything necessary to determine the conditions applied at a given spell level.
 * Should not include anything about damage or targeting.
 */
@Serializable
data class ConditionsDescription(val definitions: String, val base: String, val heightening: String?)

/**
 * The metadata describing a Pathfinder 2E spell.
 *
 * @property name The spell's name
 * @property level The spell's base level
 * @property traditions The spell's traditions
 * @property access What is required to get access to the spell
 * @property requirements Any requirements for the spell to be cast.
 * @property source The source of the spell (i.e. the book it's from)
 * @property traits The spell's traits
 * @property checks The checks required when casting the spell
 */
@Serializable
data class BasicMetadata(
    val name: String,
    val level: Int,
    val traditions: List<Tradition>,
    val access: String? = null,
    val requirements: String? = null,
    val source: String,
    val traits: List<String>,
    val checks: List<Check>
)

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