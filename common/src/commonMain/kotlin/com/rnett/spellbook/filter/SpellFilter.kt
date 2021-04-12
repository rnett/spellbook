package com.rnett.spellbook.filter

import com.rnett.spellbook.Actions
import com.rnett.spellbook.CastActionType
import com.rnett.spellbook.Condition
import com.rnett.spellbook.Save
import com.rnett.spellbook.Spell
import com.rnett.spellbook.SpellList
import com.rnett.spellbook.SpellType
import com.rnett.spellbook.TraitKey
import kotlinx.serialization.Serializable

interface SpellFilterPart {
    fun matches(spell: Spell): Boolean
}

@Serializable
data class ConditionFilter(val name: String) : SpellFilterPart {
    override fun matches(spell: Spell): Boolean = spell.conditions.any { it.name == this.name }
}

val Condition.filter get() = ConditionFilter(name)

@Serializable
sealed class AttackTypeFilter : SpellFilterPart {
    @Serializable
    data class TargetSave(val save: Save, val isBasic: Boolean?) : AttackTypeFilter() {
        override fun matches(spell: Spell): Boolean = spell.save == save && (isBasic?.let { it == spell.basicSave } ?: true)
    }

    @Serializable
    object Attack : AttackTypeFilter() {
        override fun matches(spell: Spell): Boolean = spell.attackTrait
    }
}

/**
 * Both are inclusive
 */
@Serializable
data class LevelFilter(val min: Int = 0, val max: Int = 10) : SpellFilterPart {
    constructor(level: Int) : this(level, level)

    override fun matches(spell: Spell): Boolean = spell.level in min..max
}

@Serializable
sealed class ActionFilter : SpellFilterPart {
    @Serializable
    object Reaction : ActionFilter() {
        override fun toActions(): Actions = Actions.Reaction(null)

        override fun matches(spell: Spell): Boolean = spell.actions.isReaction
    }

    @Serializable
    object Duration : ActionFilter() {
        override fun toActions(): Actions = Actions.Time("", null)

        override fun matches(spell: Spell): Boolean = spell.actions is Actions.Time
    }

    @Serializable
    /**
     * [min] and [max] are inclusive, use 0 for free action.  Don't use negatives.
     */
    data class Constant(val actions: Int) : ActionFilter() {
        override fun toActions() = Actions.Constant(actions, null)

        override fun matches(spell: Spell): Boolean = if (actions == 0)
            spell.actions.isFreeAction
        else
            spell.actions.canCastWithActions(actions)
    }

    abstract fun toActions(): Actions

    companion object {
        val Free = Constant(0)
        val Single = Constant(1)
        val Double = Constant(2)
        val Triple = Constant(3)
    }
}

fun <T : SpellFilterPart> defaultOrFilter(): Filter<T> = Filter(emptyList(), Operation.OR, Operation.OR, false)

fun <T : SpellFilterPart> defaultAndFilter(): Filter<T> = Filter(emptyList(), Operation.AND, Operation.OR, false)

class Ander {
    @Deprecated("Internal")
    var result: Boolean = true

    inline fun and(clause: () -> Boolean) {
        if (result) {
            if (!clause()) {
                result = false
            }
        }
    }

    operator fun (() -> Boolean).unaryPlus() = and(this)

    fun result() = result
}

inline fun and(block: Ander.() -> Unit): Boolean =
    Ander().apply(block).result()

//TODO area, range, duration.
// damage, but need in spell
// option to show hightenable spells at their hightened levels like AoN
@Serializable
//TODO should be @Immutable for compose
data class SpellFilter(
    val lists: Filter<SpellList> = defaultOrFilter(),
    val isFocus: Boolean? = false,
    val attackTypes: Filter<AttackTypeFilter> = defaultOrFilter(),
    val level: LevelFilter = LevelFilter(1, 10),
    val types: Filter<SpellType> = defaultAndFilter(),
    val traits: Filter<TraitKey> = defaultOrFilter(),
    val actions: Filter<ActionFilter> = defaultOrFilter(),
    val hasActionTypes: Set<CastActionType>? = null,
    val doesntHaveActionTypes: Set<CastActionType>? = null,
    val sustained: Boolean? = null,
    val hasSummons: Boolean? = null,
    val hasHeightening: Boolean? = null,
    val conditions: Filter<ConditionFilter> = defaultOrFilter(),
    val schools: Filter<TraitKey> = defaultAndFilter(),
    val rarity: Filter<TraitKey> = defaultAndFilter(),
    val hasManipulate: Boolean? = null,
    val persistentDamage: Boolean? = null,
) : SpellFilterPart {
    override fun matches(spell: Spell): Boolean =
        and {
            +{ lists.matches(spell) }
            +{ level.matches(spell) }
            +{ types.matches(spell) }

            +{ isFocus matchesIfNonNull (spell.isFocus) }
            +{ sustained matchesIfNonNull spell.sustained }
            +{ hasSummons matchesIfNonNull (spell.summons != null) }
            +{ hasHeightening matchesIfNonNull (spell.heightening != null) }

            +{ attackTypes.matches(spell) }

            +{ schools.matches(spell) }
            +{ rarity.matches(spell) }
            +{ hasManipulate matchesIfNonNull (spell.hasManipulate) }

            +{ actions.matches(spell) }
            +{ (hasActionTypes?.let { types -> spell.actionTypes.orEmpty().any { it in types } } ?: true) }
            +{ (doesntHaveActionTypes?.let { types -> spell.actionTypes.orEmpty().none { it in types } } ?: true) }
            +{ conditions.matches(spell) }
            +{ traits.matches(spell) }
            +{ persistentDamage matchesIfNonNull spell.persistentDamage }
        }

}