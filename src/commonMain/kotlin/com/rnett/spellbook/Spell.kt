package com.rnett.spellbook

import kotlinx.serialization.Serializable

//TODO differentiate between area types.

@Serializable
sealed class TargetingType(val name: String) {
    object SingleTarget : TargetingType("Single Target")
    object MultiTarget : TargetingType("Multi-Target")

    override fun toString(): String {
        return name
    }

    sealed class Area(name: String) : TargetingType(name) {
        object Cone : Area("Cone")
        object Line : Area("Line")
        object Emanation : Area("Emanation")
        object Burst : Area("Burst")
        object Wall : Area("Wall")
        object Other : Area("Area")

        companion object {
            val knownTypes = setOf(Cone, Line, Emanation, Burst, Wall)
            operator fun invoke(area: String): List<Area> {
                val found = knownTypes.filter { it.name.toLowerCase() in area.toLowerCase() }

                return if (found.isNotEmpty())
                    found
                else
                    listOf(Other)
            }
        }
    }

    object Other : TargetingType("Other")

    companion object {
        operator fun invoke(area: String?, targets: String?): List<TargetingType>? {
            return if (area != null)
                Area(area)
            else if (targets != null) {
                listOf(
                    if ("creature" in targets || "ally" in targets)
                        if ("1 " in targets)
                            SingleTarget
                        else
                            MultiTarget
                    else
                        Other
                )
            } else
                null
        }
    }
}

//@Serializable
//enum class TargetingType{
//    SingleTarget, MultiTarget, Area, Other;
//}

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
//TODO add has area/has target filters
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

    //TODO store this?  To unreliable to do much filtering by.
    val typedArea by lazy { area?.let { SpellArea(it) } }
    val targeting by lazy {
        TargetingType(area, targets)
    }
}


