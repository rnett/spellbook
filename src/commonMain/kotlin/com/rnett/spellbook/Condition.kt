package com.rnett.spellbook

import kotlinx.serialization.Serializable

//TODO I want source/descrption here?  lazy loading might be better on memory
@Serializable
data class Condition(
    val name: String,
    val source: String,
    val description: String,
    val aonId: Int,
    val positive: Boolean?
)

val uninterestingConditions =
    setOf("Encumbered", "Friendly", "Helpful", "Indifferent", "Observed", "Unfriendly", "Hostile", "Unnoticed")