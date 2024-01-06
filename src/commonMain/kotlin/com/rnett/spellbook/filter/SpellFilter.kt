package com.rnett.spellbook.filter

import com.rnett.spellbook.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
sealed class AttackType {
    @Serializable
    data class TargetSave(val save: Save, val isBasic: Boolean?) : AttackType()

    @Serializable
    object Attack : AttackType()
}

/**
 * Both are inclusive
 */
@Serializable
data class LevelFilter(val min: Int = 0, val max: Int = 10) {
    constructor(level: Int) : this(level, level)
}

@Serializable
sealed class ActionFilter {
    @Serializable
    object Reaction : ActionFilter()

    @Serializable
    object Duration : ActionFilter()

    @Serializable
    /**
     * [min] and [max] are inclusive, use 0 for free action.  Don't use negatives.
     */
    data class Actions(val min: Int, val max: Int, val acceptVariable: Boolean, val atWill: Boolean) : ActionFilter()

    companion object {
        fun Free(atWill: Boolean) = Actions(0, 0, false, atWill)
    }
}

//TODO area, range.
// damage and conditions, but need in spell
// option to show hightenable spells at their hightened levels like AoN
@Serializable
data class SpellFilter(
    val lists: Filter.Or<SpellList>? = null,
    val attackTypes: Filter.Or<AttackType>? = null,
    val level: LevelFilter? = null,
    val types: Filter.Or<SpellType>? = null,
    val traits: Filter.OrAnd<Trait>? = null,
    val actions: Filter.Or<ActionFilter>? = null,
    val hasActionTypes: Set<CastActionType>? = null,
    val doesntHaveActionTypes: Set<CastActionType>? = null,
    val sustained: Boolean? = null,
    val hasSummons: Boolean? = null,
    val hasHeightening: Boolean? = null,
    val conditions: Filter.OrAnd<String>? = null,
    val schools: Filter.Or<School>? = null,
    val rarity: Filter.Or<Rarity>? = null,
    val hasManipulate: Boolean? = null
) {
    @Transient
    val allTraits = traits?.flatten()?.toSet().orEmpty()// + schools.orEmpty() + rarity.orEmpty()
}