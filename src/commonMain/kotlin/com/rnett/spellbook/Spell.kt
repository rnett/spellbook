package com.rnett.spellbook

import kotlinx.serialization.Serializable

@Serializable
enum class SpellList(val normalList: Boolean) {
    Arcane(true),
    Divine(true),
    Occult(true),
    Primal(true),
    Focus(true),
    Other(false);

    companion object {
        val lists by lazy { values().toSet() }
    }

    override fun toString(): String {
        return "SpellList($name)"
    }
}

@Serializable
enum class CastActionType {
    Material, Somatic, Verbal, Focus;
}

@Serializable
enum class SpellType {
    Cantrip, Spell, Focus;
}

//TODO track conditions
//TODO track source by book, allow filtering
//TODO add persistent damage flag (and to filter)
@Serializable
data class Spell(
    val name: String,
    val level: Int,
    val aonId: Int,
    val type: SpellType,
    val lists: Set<SpellList>,
    val traits: Set<Trait>,
    val save: Save?,
    val basicSave: Boolean,
    val requiresAttackRoll: Boolean,
    val source: String,
    val actions: Actions,
    val actionTypes: List<CastActionType>?,
    val requirements: String?,
    val range: String?,
    val targets: String?,
    val duration: String?,
    val sustained: Boolean,
    val area: String?,
    val description: String,
    val heightening: Heightening?,
    val summons: Summons?,
    val postfix: String?,
    val spoilersFor: String?,
    val conditions: Set<Condition>
) {
    val school: School? by lazy { traits.singleOrNull { it is School } as School? }
    val rarity: Rarity by lazy { traits.singleOrNull { it is Rarity } as Rarity? ?: Rarity.Common }
    val attackTrait: Boolean by lazy { Trait.Attack in traits }
    val hasManipulate by lazy { actionTypes?.any { it == CastActionType.Material || it == CastActionType.Somatic || it == CastActionType.Focus } == true }
    val hasTrigger by lazy { actions.hasTrigger }
}


