package com.rnett.spellbook.extractor.advanced.duration

import kotlinx.serialization.Serializable


/**
 * How long a spell lasts.
 *
 * @property varies Whether the duration varies
 * @property time If the duration is a set time, the time of the duration
 * @property until If the duration is until something, what it is until
 */
@Serializable
data class Duration(
    val varies: Boolean = false,
    val time: DurationTime? = null,
    val until: DurationUntil? = null
)

/**
 * The set time a duration lasts. For example, 1 round, or 10 minutes.
 *
 * @property value The value of the duration
 * @property units The units of the duration
 */
@Serializable
data class DurationTime(val value: Int, val units: DurationUnit)

/**
 * Possible units of spell duration time
 */
@Serializable
enum class DurationUnit {
    Rounds, Minutes, Hours, Days;
}

/**
 * A condition that a spell can last until.
 *
 * @property condition A text description of the condition that causes the end of the spell
 * @property specificDuration If the spell lasts a specific, turn-related duration, that duration. For example, the end of target's next turn.
 * @property untilDismissed True if the spell lasts until you dismiss it
 */
@Serializable
data class DurationUntil(
    val condition: String,
    val specificDuration: SpecificDuration? = null,
    val untilDismissed: Boolean = false
)

/**
 * Specific, turn-based durations a spell may last.
 * If start or end is not specified (i.e. until your next turn), it means start.
 */
@Serializable
enum class SpecificDuration {
    StartOfYourNextTurn, EndOfYourNextTurn, StartOfTargetsNextTurn, EndOfTargetsNextTurn
}