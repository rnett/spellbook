package com.rnett.spellbook.krosstalk

import com.rnett.spellbook.Condition
import com.rnett.spellbook.Spell
import com.rnett.spellbook.Trait
import com.rnett.spellbook.db.Conditions
import com.rnett.spellbook.db.DbCondition
import com.rnett.spellbook.db.DbTrait
import com.rnett.spellbook.db.getSpellsForFilter
import com.rnett.spellbook.filter.SpellFilter
import kotlinx.serialization.builtins.SetSerializer
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

actual suspend fun getSpells(filter: SpellFilter): Set<Spell> {
    return newSuspendedTransaction {
        getSpellsForFilter(filter)
    }
}

actual suspend fun getAllSpells(): Set<Spell> {
    return newSuspendedTransaction {
        getSpells(SpellFilter())
    }
}

actual suspend fun getAllTraitsJson(): String {
    return newSuspendedTransaction {
        jsonSerializer.encodeToString(SetSerializer(Trait.serializer()), DbTrait.all().map { it.toTrait() }.toSet())
    }
}

actual suspend fun getAllConditionNames(): Set<String> {
    return newSuspendedTransaction {
        Conditions.slice(Conditions.id).selectAll().map { it[Conditions.id].value }.toSet()
    }
}

actual suspend fun getAllConditions(): Set<Condition> {
    return newSuspendedTransaction {
        DbCondition.all().map { it.toCondition() }.toSet()
    }
}

actual suspend fun getCondition(name: String): Condition? {
    return newSuspendedTransaction {
        DbCondition.findById(name)?.toCondition()
    }
}