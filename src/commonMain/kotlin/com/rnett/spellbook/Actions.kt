package com.rnett.spellbook

import kotlinx.serialization.Serializable

@Serializable
sealed class Actions {
    abstract val trigger: String?

    @Serializable
    data class Constant(val actions: Int, override val trigger: String?) : Actions()

    @Serializable
    data class Variable(val min: Int, val max: Int, override val trigger: String?) : Actions()

    @Serializable
    data class Reaction(override val trigger: String?) : Actions()

    @Serializable
    data class Time(val text: String, override val trigger: String?) : Actions()

    val isReaction get() = this is Reaction
    val isFreeAction get() = this is Constant && actions == 0
    val hasTrigger get() = trigger != null
}