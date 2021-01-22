package com.rnett.spellbook.db

import com.rnett.spellbook.Spell
import com.rnett.spellbook.SpellList
import com.rnett.spellbook.filter.AttackType
import com.rnett.spellbook.filter.Filter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.load.loggedTransaction
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.booleanParam
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.selectAll

object SpellbookDB {
    val db by lazy {
        Database.connect(
            "jdbc:pgsql://localhost:5432/com.rnett.spellbook", driver = "com.impossibl.postgres.jdbc.PGDriver",
            user = "postgres", password = "password"
        )
    }

    val json by lazy { Json { } }

    fun init() {
        db.also { Unit }
    }
}

inline fun <T> Query.andConditionOn(base: T?, crossinline condition: SqlExpressionBuilder.(T) -> Op<Boolean>) = ifLet(base != null) {
    it.andWhere {
        condition(base!!)
    }
}

inline fun <T : Filter<K>, K> Query.andConditionOnCompound(base: T?, crossinline condition: SqlExpressionBuilder.(K) -> Op<Boolean>) =
    andConditionOn(base) {
        it.compound { condition(it) }
    }

//TODO it's slow, but I don't think the query is the problem
fun getSpellsForFilter(filter: SpellFilter): Set<Spell> {
    //TODO make view
    val baseTable = Spells
        .leftJoin(SpellConditions)
        .leftJoin(SpellTraits)
        .leftJoin(SpellLists)
        .leftJoin(Traits)
        .leftJoin(Conditions)
//        .ifLet(filter.conditions != null) {
//            val allConditions = filter.conditions!!.flatten().toSet()
//
//            it.joinQuery({ Spells.id eq it[SpellConditions.spell] }) {
//                SpellConditions.select { SpellConditions.condition inList allConditions }
//            }
//        }
//        .ifLet(filter.allTraits.isNotEmpty()) {
//            val allTraits = filter.allTraits.map { it.name }
//
//            it.joinQuery({ Spells.id eq it[SpellTraits.spell] }) {
//                SpellTraits.select { SpellTraits.trait inList allTraits }
//            }
//        }
//        .ifLet(filter.lists != null) {
//            val allLists = filter.lists!!
//
//            it.joinQuery({ Spells.id eq it[SpellLists.spell] }) {
//                SpellLists.select { SpellLists.spellList inList allLists }
//            }
//        }

    // lists are done in join

    val query = baseTable.selectAll()
        .andConditionOnCompound(filter.attackTypes) {
            Spells.isAttackType(it)
        }
        .andConditionOn(filter.level) {
            Spells.level lessEq it.max and (Spells.level greaterEq it.min)
        }
        .andConditionOnCompound(filter.types) {
            Spells.type eq it
        }
        .andConditionOnCompound(filter.lists) {
            SpellLists.spellList eq it
        }
        .andConditionOnCompound(filter.traits) {
            SpellTraits.trait eq it.name
        }
        .andConditionOnCompound(filter.actions) {
            Spells.hasActions(it)
        }
        .andConditionOn(filter.hasActionTypes) {
            Spells.actionTypesJson.isNotNull() and it.map { Spells.actionTypesJson.like("%$it%") as Op<Boolean> }.reduce { a, b -> a and b }
        }
        .andConditionOn(filter.doesntHaveActionTypes) {
            Spells.actionTypesJson.isNull() or it.map { Spells.actionTypesJson.notLike("%$it%") as Op<Boolean> }.reduce { a, b -> a and b }
        }.andConditionOn(filter.sustained) {
            Spells.sustained eq it
        }.andConditionOn(filter.hasSummons) {
            Spells.summonsJson.isNotNull() eq booleanParam(it)
        }.andConditionOn(filter.hasHeightening) {
            Spells.heighteningJson.isNotNull() eq booleanParam(it)
        }.andConditionOnCompound(filter.conditions) {
            SpellConditions.condition eq it
        }.andConditionOnCompound(filter.schools) {
            SpellTraits.trait eq it.name
        }.andConditionOnCompound(filter.rarity) {
            SpellTraits.trait eq it.name
        }.andConditionOn(filter.hasManipulate) {
            Spells.hasManipulate eq it
        }
        .orderBy(Spells.level to SortOrder.ASC, Spells.id to SortOrder.ASC)
        .withDistinct(true)

    //TODO extract names manually and query for all?  i.e. use result row to find all needed trait ids, get all Traits for those
    val spells = query.groupBy {
        it[Spells.id]
    }.mapKeys {
        DbSpell.wrapRow(it.value.first())
    }.map { (dbSpell, rows) ->
        val traits = rows.distinctBy { it[Traits.id] }.map { DbTrait.wrapRow(it) }
        val lists = rows.map { it[SpellLists.spellList] }.toSet()
        val conditions = rows.distinctBy { it[Conditions.id] }
            .filter { it[Conditions.id] != null }.map {
                DbCondition.wrapRow(it)
            }
        dbSpell.toSpell(lists, traits, conditions)
    }


    return spells.toSet()
}

fun main() {
    SpellbookDB.init()

    loggedTransaction {
        getSpellsForFilter(SpellFilter(attackTypes = Filter.Or(AttackType.Attack), lists = Filter.Or(SpellList.Arcane))).forEach {
            println(it.name)
        }
    }

}