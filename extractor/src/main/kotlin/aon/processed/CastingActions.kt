package com.rnett.spellbook.extractor.aon.processed

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class CastingActions {
    /**
     * @property actions -1 for reaction, 0 for free
     */
    @Serializable
    @SerialName("single")
    data class Single(val actions: Int) : CastingActions()

    @Serializable
    @SerialName("variable")
    data class Variable(val min: Int, val max: Int) : CastingActions()

    @SerialName("minutes")
    @Serializable
    data class Minutes(val amount: Int) : CastingActions()

    companion object {
        fun parseAon(aonActions: String): CastingActions {
            return when (aonActions.lowercase()) {
                "reaction" -> Single(-1)
                "free action" -> Single(0)
                "single action" -> Single(1)
                "two actions" -> Single(2)
                "three actions" -> Single(3)
                "1 minute" -> Minutes(1)
                "5 minutes" -> Minutes(5)
                "10 minutes" -> Minutes(10)
                "30 minutes" -> Minutes(30)
                "1 hour" -> Minutes(60)
                "single action to three actions" -> Variable(1, 3)
                "single action or more actions" -> Variable(1, 3)
                "single action to two actions" -> Variable(1, 2)
                "single action or two actions" -> Variable(1, 2)
                "two actions to 2 rounds" -> Variable(1, 6)
                "two actions or three actions" -> Variable(2, 3)
                else -> error("Unknown actions: $aonActions")
            }
        }
    }
}

