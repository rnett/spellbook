package com.rnett.spellbook.extractor.advanced.targeting

import kotlinx.serialization.Serializable

/**
 * The targeting information about a spell
 *
 * @property range The spell's range, if it has one
 * @property targets The spell's targets, if it has one
 * @property sustained Whether the spell is sustained.
 * @property areas The spell's area(s), if it has one or more.  Some spells have more than one possible area
 */
@Serializable
data class SpellTargeting(
    val targets: Targets,
    val sustained: Boolean = false,
    val range: String? = null,
    val areas: List<Area>? = null,
)

/**
 * The number and type of targets of a spell.
 *
 * @property type The type of targets, e.g. humanoids, creatures, hostile creatures.
 * @property min The minimum amount of targets, or null if there is no minimum
 * @property max The maximum amount of targets, or null if there is no maximum
 * @property self True if the spell is only cast on yourself
 */
@Serializable
data class Targets(val type: String, val min: Int?, val max: Int?, val self: Boolean = false)

/**
 * The area a spell effects.
 * Some areas may have well-defined types and sizes, in which case just [type] and [size] should be used.
 * If the area is not well-defined, put its text in [description].
 *
 * @property type The type of the area, if it is a well-defined area
 * @property size The size of the area, if it is a well-defined area
 * @property description The description of the area, if it is not a well-defined area
 */
@Serializable
data class Area(val type: AreaType? = null, val size: Int? = null, val description: String? = null)

/**
 * The type of area used by a spell
 */
@Serializable
enum class AreaType {
    Burst, Line, Emanation, Cone, Wall;
}
