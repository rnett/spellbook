package com.rnett.spellbook.model.spell

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
@OptIn(ExperimentalSerializationApi::class)
sealed class Heightening {
    //TODO fix names in DB
    @Serializable
    data class Every(val every: Int, @JsonNames("highten") val heighten: String) : Heightening()

    @Serializable
    data class Specific(@JsonNames("heightenings") val heightening: Map<Int, String>) : Heightening() {
        operator fun get(level: Int): String? = heightening[level]
    }
}