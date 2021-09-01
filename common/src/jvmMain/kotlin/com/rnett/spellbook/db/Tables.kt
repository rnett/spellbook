package com.rnett.spellbook.db

import com.rnett.spellbook.filter.AttackTypeFilter
import com.rnett.spellbook.spell.Actions
import com.rnett.spellbook.spell.CastActionType
import com.rnett.spellbook.spell.Condition
import com.rnett.spellbook.spell.Heightening
import com.rnett.spellbook.spell.Rarity
import com.rnett.spellbook.spell.Save
import com.rnett.spellbook.spell.School
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import com.rnett.spellbook.spell.Summons
import com.rnett.spellbook.spell.Trait
import com.rnett.spellbook.spell.TraitKey
import kotlinx.serialization.builtins.ListSerializer
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.not
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll

val allTables = listOf(Traits, Conditions, Spells, SpellTraits, SpellLists, SpellConditions)

object Traits : StringIdTable("traits", "name", 50) {
    val aonId = integer("aon_id")
    val description = text("description")
}

class DbTrait(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<DbTrait>(Traits)

    val name get() = id.value
    var aonId by Traits.aonId
    var description by Traits.description

    fun toTrait() = Trait(name, aonId, description)
}

object SpellTraits : Table("spell_traits") {
    val spell =
        reference("spell", Spells, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE).index()
    val trait =
        reference("trait", Traits, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE).index()

    override val primaryKey = PrimaryKey(spell, trait)
}

object SpellLists : Table("spell_lists") {
    val spell =
        reference("spell", Spells, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE).index()
    val spellList = enumerationByName("list", 20, SpellList::class)

    override val primaryKey = PrimaryKey(spell, spellList)
}

object Conditions : StringIdTable("conditions", "name", 50) {
    val conditionSource = varchar("source", 100)
    val description = text("description")
    val aonId = integer("aon_id")
    val positive = bool("positive").nullable()

    val all by lazy { DbCondition.all().map { it.toCondition() } }
}

class DbCondition(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<DbCondition>(Conditions)

    val name get() = id.value
    var source by Conditions.conditionSource
    var description by Conditions.description
    var aonId by Conditions.aonId
    var positive by Conditions.positive

    fun toCondition() = Condition(name, source, description, aonId, positive)
}

object SpellConditions : Table("spell_conditions") {
    val spell =
        reference("spell", Spells, onDelete = ReferenceOption.CASCADE, onUpdate = ReferenceOption.CASCADE).index()
    val condition = reference(
        "condition",
        Conditions,
        onDelete = ReferenceOption.CASCADE,
        onUpdate = ReferenceOption.CASCADE
    ).index()

    override val primaryKey = PrimaryKey(spell, condition)
}

object Spells : StringIdTable("spells", "name", 200) {
    val name = primaryKey.columns[0] as Column<EntityID<String>>
    val level = integer("level").index()
    val aonId = integer("aon_id")
    val type = enumerationByName("type", 20, SpellType::class).index()
    val save = enumerationByName("save", 20, Save::class).nullable().index()
    val basicSave = bool("basic_save")
    val requiresAttackRoll = bool("required_attack_roll").index()
    val spellSource = varchar("source", 100)
    val minActions = integer("min_actions").index()
    val maxActions = integer("max_actions").index()
    val variableActions = bool("variable_actions").index()
    val timeText = varchar("time_text", 200).nullable().index()
    val trigger = varchar("trigger", 500).nullable()
    val actionTypesJson = varchar("action_types_json", 200).nullable()
    val hasManipulate = bool("has_manipulate").index()
    val requirements = varchar("requirements", 300).nullable()
    val range = varchar("range", 200).nullable()
    val targets = varchar("targets", 200).nullable()
    val duration = varchar("duration", 200).nullable()
    val sustained = bool("sustained").index()
    val area = varchar("area", 200).nullable()
    val description = text("description")
    val heighteningJson = varchar("heightening_json", 1000).nullable()
    val rarity = varchar("rarity", 20).index()
    val school = varchar("school", 20).nullable().index()
    val summonsJson = text("summons").nullable().index()
    val postfix = text("postfix").nullable()
    val spoilers = varchar("spoilers", 200).nullable()

    fun isActions(actions: Int) = sqlExpression { not(variableActions).and(maxActions eq actions) }
    val isFreeAction get() = sqlExpression { not(variableActions).and(maxActions eq 0) }
    val isReaction get() = sqlExpression { not(variableActions).and(maxActions eq -1) }
    val isVariableActions: Op<Boolean> get() = sqlExpression { variableActions eq true }
    val isTimeActions: Op<Boolean> get() = sqlExpression { timeText.isNotNull() }
    val isNormalOrFreeActions get() = sqlExpression { minActions greater -1 }
    fun isActionsLessThan(actions: Int, includeReactions: Boolean = false): Op<Boolean> = sqlExpression {
        val max = maxActions less actions
        if (includeReactions)
            max and (minActions greater -2)
        else
            max and (minActions greater -1)

    }

    val hasTrigger: Op<Boolean> get() = sqlExpression { trigger.isNotNull() }

    //    fun isRarity(rarity: Rarity) = sqlExpression { this@Spells.rarity eq rarity.name }
//    fun isSchool(school: School) = sqlExpression { this@Spells.school eq school.name }
    val hasSummons: Op<Boolean> get() = sqlExpression { summonsJson.isNotNull() }

    fun allSpoilers() = Spells.slice(Spells.spoilers).selectAll().withDistinct(true).mapNotNull { it[spoilers] }

    fun isAttackType(attackType: AttackTypeFilter): Op<Boolean> = sqlExpression {
        when (attackType) {
            is AttackTypeFilter.TargetSave -> this@Spells.save eq attackType.save
            AttackTypeFilter.Attack -> this@Spells.requiresAttackRoll eq true
            else -> error("Unknown attack type $attackType")
        }
    }

//    fun hasActions(actions: ActionFilter): Op<Boolean> = sqlExpression {
//        when (actions) {
//            ActionFilter.Reaction -> this@Spells.isReaction
//            ActionFilter.Duration -> this@Spells.isTimeActions
//            is ActionFilter.ActionRange -> {
//                (isNormalOrFreeActions and (maxActions greaterEq actions.min) and (minActions lessEq actions.max))
//                    .ifLet(!actions.acceptVariable) {
//                        it and not(isVariableActions)
//                    }
//                    .ifLet(actions.atWill) {
//                        not(hasTrigger)
//                    }
//            }
//        }
//    }

}

class DbSpell(id: EntityID<String>) : StringEntity(id) {
    companion object : StringEntityClass<DbSpell>(Spells)

    val name get() = id.value
    var level by Spells.level
    var aonId by Spells.aonId
    var type by Spells.type
    var save by Spells.save
    var basicSave by Spells.basicSave
    var requiresAttackRoll by Spells.requiresAttackRoll
    var source by Spells.spellSource
    private var minActions by Spells.minActions
    private var maxActions by Spells.maxActions
    private var variableActions by Spells.variableActions
    private var timeText by Spells.timeText
    private var trigger by Spells.trigger
    private var actionTypesJson by Spells.actionTypesJson
    private var hasManipulate by Spells.hasManipulate
    var requirements by Spells.requirements
    var range by Spells.range
    var targets by Spells.targets
    var duration by Spells.duration
    var sustained by Spells.sustained
    var area by Spells.area
    var description by Spells.description
    private var heighteningJson by Spells.heighteningJson
    private var rarity by Spells.rarity
    private var school by Spells.school
    private var summonsJson by Spells.summonsJson
    var postfix by Spells.postfix
    var spoilers by Spells.spoilers

    private var traitsSI by DbTrait via SpellTraits
    private var conditionsSI by DbCondition via SpellConditions

//    var dbTraits
//        get() = traitsSI.toSet()
//        set(value) {
//            traitsSI = SizedCollection(value)
//        }
//
//    var dbSpellLists
//        get() = spellListsSI.toSet()
//        set(value) {
//            spellListsSI = SizedCollection(value)
//        }

    var traits
        get() = traitsSI.map { Trait(it.name, it.aonId, it.description) }.toSet()
        @Deprecated("Will force a flush, you probably want to use batch updates")
        set(value) {
            setSpecialTraits(value.map { it.key })
            traitsSI = SizedCollection(value.map { DbTrait[it.name] })
        }

    fun setSpecialTraits(traits: Iterable<TraitKey>) {
        rarity = (traits.singleOrNull { it in Rarity })?.name ?: Rarity.Common.name
        school = (traits.singleOrNull { it in School })?.name
    }

    var spellLists
        get() = SpellLists.slice(SpellLists.spellList).select { SpellLists.spell eq id }
            .map { it[SpellLists.spellList] }.toSet()
        @Deprecated("Will force a flush, you probably want to use batch updates")
        set(value) {
            SpellLists.deleteWhere { SpellLists.spell eq id }
            SpellLists.batchInsert(value, shouldReturnGeneratedValues = false) {
                this[SpellLists.spell] = id
                this[SpellLists.spellList] = it
            }
        }

    var _conditions
        get() = conditionsSI.toSet()
        set(value) {
            conditionsSI = SizedCollection(value)
        }

    var conditions
        get() = _conditions.map { it.toCondition() }.toSet()
        set(value) {
            _conditions = value.map { DbCondition[it.name] }.toSet()
        }

    var actionTypes
        get() = actionTypesJson?.let {
            SpellbookDB.json.decodeFromString(
                ListSerializer(CastActionType.serializer()),
                it
            )
        }
        set(value) {
            if (value != null) {
                actionTypesJson = SpellbookDB.json.encodeToString(ListSerializer(CastActionType.serializer()), value)
                hasManipulate =
                    value.any { it == CastActionType.Material || it == CastActionType.Somatic || it == CastActionType.Focus }
            } else {
                actionTypesJson = null
                hasManipulate = false
            }
        }

    var heightening
        get() = heighteningJson?.let { SpellbookDB.json.decodeFromString(Heightening.serializer(), it) }
        set(value) {
            heighteningJson = value?.let { SpellbookDB.json.encodeToString(Heightening.serializer(), it) }
        }

    var summons: Summons?
        get() = summonsJson?.let { SpellbookDB.json.decodeFromString(Summons.serializer(), it) }
        set(value) {
            summonsJson = value?.let { SpellbookDB.json.encodeToString(Summons.serializer(), it) }
        }

    var actions: Actions
        get() = when {
            timeText != null -> Actions.Time(timeText!!, trigger)
            variableActions -> Actions.Variable(minActions, maxActions, trigger)
            maxActions == -1 -> Actions.Reaction(trigger)
            else -> Actions.Constant(maxActions, trigger)
        }
        set(value) {
            trigger = value.trigger
            when (value) {
                is Actions.Constant -> {
                    minActions = value.actions
                    maxActions = value.actions
                    variableActions = false
                    timeText = null
                }
                is Actions.Variable -> {
                    minActions = value.min
                    maxActions = value.max
                    variableActions = true
                    timeText = null
                }
                is Actions.Reaction -> {
                    minActions = -1
                    maxActions = -1
                    variableActions = false
                    timeText = null
                }
                is Actions.Time -> {
                    timeText = value.text
                    minActions = -2
                    maxActions = -2
                    variableActions = false
                }
            }
        }

    fun toSpell(
        spellLists: Iterable<SpellList> = this.spellLists,
        traits: Iterable<Trait> = this.traits,
        conditions: Iterable<Condition> = this.conditions,
    ) = Spell(
        name,
        level,
        aonId,
        type,
        spellLists.toSet(),
        traits.toSet(),
        save,
        basicSave,
        requiresAttackRoll,
        source,
        actions,
        actionTypes,
        requirements,
        range,
        targets,
        duration,
        sustained,
        area,
        description,
        heightening,
        summons,
        postfix,
        spoilers,
        conditions.toSet()
    )
}