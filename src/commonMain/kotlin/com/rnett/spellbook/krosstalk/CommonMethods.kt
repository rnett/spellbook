package com.rnett.spellbook.krosstalk

import com.rnett.krosstalk.annotations.KrosstalkEndpoint
import com.rnett.krosstalk.annotations.KrosstalkMethod
import com.rnett.spellbook.*
import com.rnett.spellbook.filter.SpellFilter
import kotlinx.serialization.builtins.SetSerializer

@KrosstalkMethod(MyKrosstalk::class)
@KrosstalkEndpoint("/spells", "POST")
expect suspend fun getSpells(filter: SpellFilter): Set<Spell>

@KrosstalkMethod(MyKrosstalk::class)
@KrosstalkEndpoint("/spells", "GET")
expect suspend fun getAllSpells(): Set<Spell>

//TODO do this directly once serialization is fixed
@KrosstalkMethod(MyKrosstalk::class)
@KrosstalkEndpoint("/traits", "GET")
expect suspend fun getAllTraitsJson(): String

suspend fun getAllTraits() = jsonSerializer.decodeFromString(SetSerializer(Trait.serializer()), getAllTraitsJson())

fun getAllSchools(): Set<School> = School.schools

fun getAllRarities(): Set<Rarity> = Rarity.rarities

fun getAllSpellLists(): Set<SpellList> = SpellList.lists

@KrosstalkMethod(MyKrosstalk::class)
@KrosstalkEndpoint("/conditionNames", "GET")
expect suspend fun getAllConditionNames(): Set<String>

@KrosstalkMethod(MyKrosstalk::class)
@KrosstalkEndpoint("/conditions", "GET")
expect suspend fun getAllConditions(): Set<Condition>

@KrosstalkMethod(MyKrosstalk::class)
@KrosstalkEndpoint("/conditions", "POST")
expect suspend fun getCondition(name: String): Condition?