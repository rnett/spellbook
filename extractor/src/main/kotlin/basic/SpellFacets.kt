package com.rnett.spellbook.extractor.basic

import com.rnett.spellbook.extractor.advanced.Check
import kotlinx.serialization.Serializable

/*
Classes representing the different facets of a spell, and SpellFacets which collects them

Each field of SpellFacets represents an independent facet of the spell, and as such should be as self-contained as possible. They will be converted into structured data later, independently of each-other.
The only information from the overall spell that will be provided for these later conversions are the name, level, and checks - everything else should be included in the description fields.
This includes differences based on the degree of success.

Fields do not need to closely match the spell's text in format or style.
Instead, they should focus on precision, accuracy, and completeness.
All string fields may be multi-line paragraphs, like the body of the spell.
Make them as long as necessary to include all the relevant information.
Analyze the spell carefully to determine what should be contained in each facet.
Analyze each facet of the spell separately.
 */

/**
 * A representation of the different facets spell. This includes all data about how the spell works.
 *
 * Each field of SpellFacets represents an independent facet of the spell, and as such should be as self-contained as possible. They will be converted into structured data later, independently of each-other.
 * The only information from the overall spell that will be provided for these later conversions are the name, level, and checks - everything else should be included in the description fields.
 * This includes differences based on the degree of success.
 *
 * @property basicMetadata Basic metadata about the spell
 * @property duration The duration of the spell, including any other information about how long it lasts, when it ends, or whether it is sustained. This includes anything a caster might need to know to determine how long the spell lasts and how to maintain it
 * @property targeting The targeting of the spell. This includes the range and area. This includes anything a caster might need to know to determine what is affected by the spell
 * @property actions The actions used to cast the spell. This includes anything a caster might need to know about what is required to cast the spell.
 * @property damage A description of any damage done by the spell, if any is done. This includes anything a caster might need to know to determine the damage done by any casting of the spell
 * @property conditions A description of any conditions applied by the spell, if any. This includes anything a caster might need to know to determine the conditions applied by any casting of the spell
 */
@Serializable
data class SpellFacets(
    val basicMetadata: BasicMetadata,
    val duration: String?,
    val targeting: Targeting,
    val actions: String,
    val damage: Damage?,
    val conditions: Conditions?,
)

/**
 * How a spell is targeted, and how that changes as the spell is heightened.
 * Must include everything necessary to calculate the conditions applied under any conditions, including the degree of success and when heightened.
 *
 * @property base The targeting at the base level
 * @property heightening How the targeting changes as the spell is heightened, if it does. Should include the levels of heightening.
 * Should include everything necessary to determine the targeting at a given spell level.
 * Should not include anything about damage or conditions.
 */
@Serializable
data class Targeting(val base: String, val heightening: String?)

/**
 * The damage done by a spell.
 * Must include everything necessary to calculate the damage done under any conditions, including the degree of success and when heightened.
 *
 * @property definitions Any definitions that are shared between the base damage and the heightening.
 * @property base The base damage done
 * @property heightening How the damage is heightened. Should include the levels of heightening
 * Should include everything necessary to determine the damage done at a given spell level.
 * Should not include anything about targeting or conditions.
 */
@Serializable
data class Damage(val definitions: String, val base: String, val heightening: String)

/**
 * The conditions applied by a spell.
 * Must include everything necessary to calculate the conditions applied under any conditions, including the degree of success and when heightened.
 *
 * @property definitions Any condition-related definitions from the spell. For example, custom afflictions (conditions, diseases, curses, etc.) defined by the spell (i.e. with stages defined by the spell). Any new conditions defined by the spell should be explained here.
 * Must include each stage of the condition.
 * @property base The base conditions applied by the spell
 * @property heightening How the conditions change as the spell is heightened, if they do. Should include the levels of heightening.
 * Should include everything necessary to determine the conditions applied at a given spell level.
 * Should not include anything about damage or targeting.
 */
@Serializable
data class Conditions(val definitions: String, val base: String, val heightening: String?)

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