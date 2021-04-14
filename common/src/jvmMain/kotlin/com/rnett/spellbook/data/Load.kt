package com.rnett.spellbook.data

import com.rnett.spellbook.filter.DURATION_IN_DESCRIPTION
import com.rnett.spellbook.spell.Rarity
import com.rnett.spellbook.spell.School
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.Trait
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object Loader {
    val json = Json { }
    inline fun <reified T> readResourceAs(file: String) = json.decodeFromString<T>(readResource(file))
    fun readResource(file: String): String = this::class.java.getResource(file)?.readText() ?: error("Resource $file does not exist")
}

val allSpells by lazy { Loader.readResourceAs<List<Spell>>("/data/spells.json") }

val allTraits by lazy { Loader.readResourceAs<List<Trait>>("/data/traits.json") }
val allSpellTraits by lazy { allSpells.flatMap { it.traits }.distinct() }
val nonSpecialSpellTraits by lazy { allSpellTraits.filterNot { it in Rarity || it in School }.sortedBy { it.name } }
val traitsByName by lazy { allTraits.associateBy { it.name } }

//val allConditions by lazy { Loader.readResourceAs<List<Condition>>("/data/conditions.json") }

val allSpellConditions by lazy { allSpells.flatMap { it.conditions }.distinct() }
val interestingSpellConditions by lazy { allSpellConditions.filter { it.isInteresting }.toSet() }
val spellConditionsByName by lazy { interestingSpellConditions.associateBy { it.name } }


val allDurations by lazy {
    allSpells.map { it.duration }.filterNot { it != null && "see" in it }.toSet() + DURATION_IN_DESCRIPTION
}

val allTargeting by lazy {
    allSpells.flatMap { it.targeting ?: emptyList() }.toSet()
}