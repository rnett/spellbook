package com.rnett.spellbook

import com.rnett.spellbook.db.SpellbookDB

fun main() {
    SpellbookDB.initPostgres()
    SpellbookDB.initH2()
    SpellbookDB.updateDB(SpellbookDB.postgresDB!!, SpellbookDB.h2DB!!)
}