package com.rnett.spellbook.spell

import com.rnett.spellbook.resourcePrefix
import kotlinx.serialization.Serializable

@Serializable
sealed class Actions {
    abstract val trigger: String?

    @Serializable
    data class Constant(val actions: Int, override val trigger: String?) : Actions()

    /**
     * [min] and [max] are inclusive.  Max may be 6 for 2 round spells
     */
    @Serializable
    data class Variable(val min: Int, val max: Int, override val trigger: String?) : Actions()

    @Serializable
    data class Reaction(override val trigger: String?) : Actions()

    @Serializable
    data class Time(val text: String, override val trigger: String?) : Actions()

    val isReaction get() = this is Reaction
    val isFreeAction get() = this is Constant && actions == 0
    val hasTrigger get() = trigger != null

    fun canCastWithActions(actions: Int): Boolean = when (this) {
        is Variable -> actions in min..max
        is Constant -> actions == this.actions
        else -> false
    }
}

fun actionStr(actions: Int) = when (actions) {
    0 -> "Free Action"
    1 -> "1 Action"
    else -> "$actions Actions"
}

fun constantActionImg(actions: Int) = when (actions) {
    0 -> "${resourcePrefix}static/freeaction.png"
    1 -> "${resourcePrefix}static/1action.png"
    2 -> "${resourcePrefix}static/2actions.png"
    3 -> "${resourcePrefix}static/3actions.png"
    else -> error("Not a valid number of constant actions: $actions")
}
