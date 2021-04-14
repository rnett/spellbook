package com.rnett.spellbook.spell

import kotlinx.serialization.Serializable

@Serializable
sealed class Heightening {
    @Serializable
    data class Every(val every: Int, val highten: String) : Heightening()
    @Serializable
    data class Specific(val heightenings: Map<Int, String>) : Heightening() {
        operator fun get(level: Int): String? = heightenings[level]
    }
}