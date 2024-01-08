package com.rnett.spellbook.model.spell

import kotlinx.serialization.Serializable

//TODO I want source/descrption here?  lazy loading might be better on memory
@Serializable
data class Condition(
    val name: String,
    val source: String,
    val description: String,
    override val aonId: Int,
    val positive: Boolean?
) : AonItem {
    val isInteresting by lazy { name !in uninterestingConditions }
    override val aonPage: String = "Conditions"
}

val uninterestingConditions = setOf(
    "Broken",
    "Encumbered",
    "Friendly",
    "Helpful",
    "Indifferent",
    "Observed",
    "Unfriendly",
    "Hostile",
    "Unnoticed"
)