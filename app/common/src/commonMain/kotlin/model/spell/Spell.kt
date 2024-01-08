package com.rnett.spellbook.model.spell

import com.rnett.spellbook.model.filter.SpellFilterPart
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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
            val knownTypes: Set<Area> by lazy { setOf(Cone, Line, Emanation, Burst, Wall) }
            operator fun invoke(area: String): List<Area> {
                val found = knownTypes.filter { it.name.lowercase() in area.lowercase() }

                return found.ifEmpty { listOf(Other) }
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
    Elemental(true),
    Focus(true),
    Other(false);

    override fun matches(spell: Spell): Boolean = this in spell.lists

    companion object {
        val lists by lazy { values().toSet() }
        val traditions by lazy { setOf(Arcane, Divine, Occult, Primal).sorted() }
        val nonFocusLists by lazy { traditions + Elemental }
    }
}

@Serializable
enum class CastActionType {
    Material, Somatic, Verbal, Focus;
}

@Serializable
enum class SpellType(val longName: String) : SpellFilterPart {
    Spell("model/spell"), Focus("focus spell");

    override fun matches(spell: com.rnett.spellbook.model.spell.Spell): Boolean = this == spell.type
}


private val spellComparator = compareBy<Spell> { it.level }.thenBy { it.type }.thenBy { it.name }

//TODO track source by book, allow filtering
//TODO add persistent damage flag (and to filter)
//TODO add has area/has target filters
@Serializable
data class Spell(
    val name: String,
    val level: Int,
    override val aonId: Int,
    val type: SpellType,
    val isCantrip: Boolean,
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
) : Comparable<Spell>, AonItem {
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

    fun isInLists(lists: Iterable<SpellList>) = lists.any { it in this.lists }

    override fun compareTo(other: Spell): Int = spellComparator.compare(this, other)
    override val aonPage: String = "Spells"
}


