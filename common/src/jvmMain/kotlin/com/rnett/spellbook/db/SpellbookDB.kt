package com.rnett.spellbook.db

import com.rnett.spellbook.data.allSpells
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.load.loggedTransaction
import com.rnett.spellbook.spell.Spell
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

object SpellbookDB {
    lateinit var db: Database
        private set

    var h2DB: Database? = null
        private set

    var postgresDB: Database? = null
        private set

    val json by lazy { Json { } }

    fun init(database: Database) {
        db = database
    }

    fun initPostgres(
        url: String = "localhost",
        port: String = "5432",
        user: String = "postgres",
        password: String = "password",
        db: String = "spellbook"
    ) {
        val database = Database.connect(
            "jdbc:pgsql://$url:$port/$db", driver = "com.impossibl.postgres.jdbc.PGDriver",
            user = user, password = password
        )
        init(database)
        postgresDB = database
    }

    fun initH2(
        file: String = "spellbook",
        user: String = "rnett",
        password: String = "thepassword",
        filePassword: String = "encrypt"
    ) {
        val database =
            Database.connect("jdbc:h2:$file;AUTO_SERVER=TRUE;CIPHER=AES", driver = "org.h2.Driver", user = user, password = "$filePassword $password")
        init(database)
        h2DB = database
    }

    fun initTables() {
        loggedTransaction {
            SchemaUtils.createMissingTablesAndColumns(Traits, SpellLists, SpellTraits, Conditions, SpellConditions, Spells)
        }
    }

    fun updateDB(from: Database, to: Database) {
        loggedTransaction(to) {
            SchemaUtils.drop(*allTables.toTypedArray())
            initTables()
        }
        allTables.forEach { table ->
            val rows = transaction(from) { table.selectAll().toList() }
            loggedTransaction(to) {
                table.batchInsert(rows, shouldReturnGeneratedValues = false) {
                    table.columns.forEach { col ->
                        this[col as Column<Any?>] = it[col]
                    }
                }
            }
        }
    }

}
//
//inline fun <T> Query.andConditionOn(base: T?, crossinline condition: SqlExpressionBuilder.(T) -> Op<Boolean>) = ifLet(base != null) {
//    it.andWhere {
//        condition(base!!)
//    }
//}
//
//inline fun <T> Query.andFilter(base: Filter<T>, crossinline condition: SqlExpressionBuilder.(T) -> Op<Boolean>) =
//    andWhere {
//        base.compound { condition(it) }
//    }
//
//inline fun <T> Query.andCompoundOr(base: Set<T>, crossinline condition: SqlExpressionBuilder.(T) -> Op<Boolean>) =
//    andWhere {
//        if(base.isEmpty())
//            Op.TRUE
//        else
//            base.map { condition(it) }.compoundOr()
//    }

//TODO anding doesn't work, since it only ever applies to single rows
fun getSpellsForFilter(filter: SpellFilter): List<Spell> {
    //TODO make view
//    val baseTable = Spells
//        .leftJoin(SpellConditions)
//        .leftJoin(SpellTraits)
//        .leftJoin(SpellLists)
//        .leftJoin(Traits)
//        .leftJoin(Conditions)
////        .ifLet(filter.conditions != null) {
////            val allConditions = filter.conditions!!.flatten().toSet()
////
////            it.joinQuery({ Spells.id eq it[SpellConditions.spell] }) {
////                SpellConditions.select { SpellConditions.condition inList allConditions }
////            }
////        }
////        .ifLet(filter.allTraits.isNotEmpty()) {
////            val allTraits = filter.allTraits.map { it.name }
////
////            it.joinQuery({ Spells.id eq it[SpellTraits.spell] }) {
////                SpellTraits.select { SpellTraits.trait inList allTraits }
////            }
////        }
////        .ifLet(filter.lists != null) {
////            val allLists = filter.lists!!
////
////            it.joinQuery({ Spells.id eq it[SpellLists.spell] }) {
////                SpellLists.select { SpellLists.spellList inList allLists }
////            }
////        }
//
//    // lists are done in join
//
//    val query = baseTable.selectAll()
//        .andFilter(filter.attackTypes) {
//            Spells.isAttackType(it)
//        }
//        .andConditionOn(filter.level) {
//            Spells.level lessEq it.max and (Spells.level greaterEq it.min)
//        }
//        .andCompoundOr(filter.types) {
//            Spells.type eq it
//        }
//        .andFilter(filter.lists) {
//            SpellLists.spellList eq it
//        }
//        .andFilter(filter.traits) {
//            SpellTraits.trait eq it.name
//        }
//        .andFilter(filter.actions) {
//            Spells.hasActions(it)
//        }
//        .andConditionOn(filter.hasActionTypes) {
//            Spells.actionTypesJson.isNotNull() and it.map { Spells.actionTypesJson.like("%$it%") as Op<Boolean> }.reduce { a, b -> a and b }
//        }
//        .andConditionOn(filter.doesntHaveActionTypes) {
//            Spells.actionTypesJson.isNull() or it.map { Spells.actionTypesJson.notLike("%$it%") as Op<Boolean> }.reduce { a, b -> a and b }
//        }.andConditionOn(filter.sustained) {
//            Spells.sustained eq it
//        }.andConditionOn(filter.hasSummons) {
//            Spells.summonsJson.isNotNull() eq booleanParam(it)
//        }.andConditionOn(filter.hasHeightening) {
//            Spells.heighteningJson.isNotNull() eq booleanParam(it)
//        }.andFilter(filter.conditions) {
//            SpellConditions.condition eq it
//        }.andCompoundOr(filter.schools) {
//            SpellTraits.trait eq it.name
//        }.andCompoundOr(filter.rarity) {
//            SpellTraits.trait eq it.name
//        }.andConditionOn(filter.hasManipulate) {
//            Spells.hasManipulate eq it
//        }
//        .orderBy(Spells.level to SortOrder.ASC, Spells.id to SortOrder.ASC)
//        .withDistinct(true)
//
//    //TODO extract names manually and query for all?  i.e. use result row to find all needed trait ids, get all Traits for those
//    val spells = query.groupBy {
//        it[Spells.id]
//    }.mapKeys {
//        DbSpell.wrapRow(it.value.first())
//    }.map { (dbSpell, rows) ->
//        val traits = rows.distinctBy { it[Traits.id] }.map { DbTrait.wrapRow(it) }
//        val lists = rows.map { it[SpellLists.spellList] }
//        val conditions = rows.distinctBy { it[Conditions.id] }
//            .filter { it[Conditions.id] != null }.map {
//                DbCondition.wrapRow(it)
//            }
//        dbSpell.toSpell(lists, traits.map { it.toTrait() }, conditions.map { it.toCondition() })
//    }
//
//    allSpells.let { println(it.size) }

    return allSpells.filter { filter.matches(it) }
}