package com.rnett.spellbook.extractor.advanced

import com.rnett.spellbook.extractor.basic.Check
import kotlinx.serialization.Serializable

/**
 * Should be provided as input when converting descriptions
 */
@Serializable
data class SpellOverview(val name: String, val level: Int, val checks: List<Check>)

/**
 * Contains the results of each possible degree of success of some check
 */
@Serializable
data class CheckDegreesOfSuccess<T>(
    val check: Check,
    val criticalSuccess: CheckResult<T>,
    val success: CheckResult<T>,
    val failure: CheckResult<T>,
    val criticalFailure: CheckResult<T>
)

/**
 * The result of some check: either an effect, or another check to perform
 */
@Serializable
data class CheckResult<T>(val effect: T?, val nextCheck: CheckDegreesOfSuccess<T>?)