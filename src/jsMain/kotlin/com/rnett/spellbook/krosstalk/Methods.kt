package com.rnett.spellbook.krosstalk

import com.rnett.krosstalk.krosstalkCall
import com.rnett.spellbook.Condition
import com.rnett.spellbook.Spell
import com.rnett.spellbook.filter.SpellFilter

actual suspend fun getSpells(filter: SpellFilter): Set<Spell> = krosstalkCall()

actual suspend fun getAllSpells(): Set<Spell> = krosstalkCall()

actual suspend fun getAllTraitsJson(): String = krosstalkCall()

actual suspend fun getAllConditionNames(): Set<String> = krosstalkCall()

actual suspend fun getAllConditions(): Set<Condition> = krosstalkCall()

actual suspend fun getCondition(name: String): Condition? = krosstalkCall()