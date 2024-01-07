package com.rnett.spellbook.extractor.advanced

import kotlinx.serialization.Serializable

/**
 * Contains the results of each possible degree of success of some check.
 * Can be nested using [CheckResult] if multiple checks are made.
 * If there are multiple checks, earlier checks usually affect the outcomes of later ones rather than the effects directly.
 *
 * The "degree" of the check outcome goes as follows, from best to worse: critical success, success, failure, critical failure.
 *
 * @property check The check being made
 * @property criticalSuccess The effects or further checks if this check is a critical success
 * @property success The effects or further checks if this check is a success
 * @property failure The effects or further checks if this check is a failure
 * @property criticalFailure The effects or further checks if this check is a critical failure
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
 * The result of some check: either an effect, or another check to perform.
 * Can be nested using [nextCheck] if there are multiple checks to make.
 * Keep in mind that the effects may depend on the results of previous checks (e.g. treating a success as a failure) - this should be reflected in the effects.
 *
 * @property effect If this is the last check made, the effects depending on the outcomes of the checks
 * @property nextCheck If there are more checks to make, the next check to make and its outcomes
 */
@Serializable
data class CheckResult<T>(val effect: T?, val nextCheck: CheckDegreesOfSuccess<T>?)

/**
 * A spell casting tradition
 */
@Serializable
enum class Check {
    SpellAttack, Fortitude, Reflex, Will;
}