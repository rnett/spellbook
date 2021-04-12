package com.rnett.spellbook.db

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

fun exportConditions(file: File) {
    val conditions = transaction {
        DbCondition.all().map { it.toCondition() }
    }
    file.writeText(Json { }.encodeToString(conditions))
}

fun exportTraits(file: File) {
    val conditions = transaction {
        DbTrait.all().map { it.toTrait() }
    }
    file.writeText(Json { }.encodeToString(conditions))
}

fun exportSpells(file: File) {
    val conditions = transaction {
        DbSpell.all().map { it.toSpell() }
    }
    file.writeText(Json { }.encodeToString(conditions))
}

fun main(args: Array<String>) {
    SpellbookDB.initPostgres()
    val dir = File(args.getOrNull(0) ?: error("Must pass one argument for the output directory"))
    dir.mkdirs()
    exportConditions(File(dir, "conditions.json"))
    exportTraits(File(dir, "traits.json"))
    exportSpells(File(dir, "spells.json"))
}