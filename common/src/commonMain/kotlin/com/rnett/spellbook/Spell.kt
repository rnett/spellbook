package com.rnett.spellbook

import com.rnett.spellbook.filter.SpellFilterPart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//TODO differentiate between area types.

@Serializable
sealed class TargetingType(open val name: String) : SpellFilterPart {

    override fun matches(spell: Spell): Boolean = spell.targeting?.contains(this) ?: false

    @Serializable
    object SingleTarget : TargetingType("Single Target")

    @Serializable
    object MultiTarget : TargetingType("Multi-Target")

    override fun toString(): String {
        return name
    }

    @Serializable
    sealed class Area(@SerialName("areaName") override val name: String) : TargetingType(name) {
        @Serializable
        object Cone : Area("Cone")

        @Serializable
        object Line : Area("Line")

        @Serializable
        object Emanation : Area("Emanation")

        @Serializable
        object Burst : Area("Burst")

        @Serializable
        object Wall : Area("Wall")

        @Serializable
        object Other : Area("Area")

        companion object {
            val knownTypes by lazy { setOf(Cone, Line, Emanation, Burst, Wall) }
            operator fun invoke(area: String): List<Area> {
                val found = knownTypes.filter { it.name.toLowerCase() in area.toLowerCase() }

                return if (found.isNotEmpty())
                    found
                else
                    listOf(Other)
            }
        }
    }

    @Serializable
    object Other : TargetingType("Other")

    companion object {
        fun targetingTypes(area: String?, targets: String?): List<TargetingType>? {
            return when {
                area != null -> Area(area)
                targets != null -> {
                    listOf(
                        if ("creature" in targets || "ally" in targets)
                            if ("1 " in targets)
                                SingleTarget
                            else
                                MultiTarget
                        else
                            Other
                    )
                }
                else -> null
            }
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
    val attackTrait: Boolean by lazy { Trait.Attack in traits }
    val hasManipulate by lazy { actionTypes?.any { it == CastActionType.Material || it == CastActionType.Somatic || it == CastActionType.Focus } == true }
    val hasTrigger by lazy { actions.hasTrigger }
    val persistentDamage: Boolean by lazy { description.contains("persistent", ignoreCase = true) }
    val incapacitation: Boolean by lazy { Trait.Incapacitation in traits }

    //TODO store this?  To unreliable to do much filtering by.
    val typedArea by lazy { area?.let { SpellArea(it) } }
    val targeting by lazy {
        TargetingType.targetingTypes(area, targets)
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


