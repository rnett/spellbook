package com.rnett.spellbook

import com.rnett.spellbook.filter.SpellFilterPart
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
enum class SpellList(val normalList: Boolean) : SpellFilterPart {
    Arcane(true),
    Divine(true),
    Occult(true),
    Primal(true),
    Focus(true),
    Other(false);

    override fun matches(spell: Spell): Boolean = this in spell.lists

    companion object {
        val lists by lazy { values().toSet() }
    }
}

@Serializable
enum class CastActionType {
    Material, Somatic, Verbal, Focus;
}

@Serializable
enum class SpellType : SpellFilterPart {
    Cantrip, Spell, Focus;

    override fun matches(spell: com.rnett.spellbook.Spell): Boolean = this == spell.type
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
    val conditions: Set<Condition>,
) : Comparable<Spell> {
    val isFocus: Boolean by lazy { SpellList.Focus in lists || type == SpellType.Focus }
    val school: Trait? by lazy { traits.singleOrNull { it in School } }
    val rarity: Trait by lazy { traits.single { it in Rarity } }
    val attackTrait: Boolean by lazy { traits.any { it eq Trait.Attack } }
    val hasManipulate by lazy { actionTypes?.any { it == CastActionType.Material || it == CastActionType.Somatic || it == CastActionType.Focus } == true }
    val hasTrigger by lazy { actions.hasTrigger }
    val persistentDamage: Boolean by lazy { description.contains("persistent", ignoreCase = true) }

    //TODO store this?  To unreliable to do much filtering by.
    val typedArea by lazy { area?.let { SpellArea(it) } }
    val targeting by lazy {
        TargetingType(area, targets)
    }

    override fun compareTo(other: Spell): Int {
        return level.compareTo(other.level) * 10000 + type.compareTo(other.type) * 100 + name.compareTo(other.name)
    }
}

object SpellComparator : Comparator<Spell> {
    override fun compare(a: Spell, b: Spell): Int {
        TODO("Not yet implemented")
    }

}


