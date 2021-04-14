package com.rnett.spellbook.spell

import kotlinx.serialization.Serializable

@Serializable
sealed class Summons {

    @Serializable
    data class Multiple(val summons: Map<Int, List<Creature>>) : Summons() {
        operator fun get(level: Int) = summons[level] ?: emptyList()
        fun belowOrAtLevel(level: Int) = summons.filter { it.key <= level }.flatMap { it.value }
    }

    @Serializable
    data class Single(val creature: Creature) : Summons()
}

@Serializable
data class Creature(val name: String, val aonId: Int)