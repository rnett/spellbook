package com.rnett.spellbook.utils

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.ln
import kotlin.math.pow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalContracts::class)
inline fun <T> T.ifLet(condition: Boolean, block: (T) -> T): T {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    return let {
        if (condition)
            block(it)
        else
            it
    }
}

suspend fun <R> withBackoff(
    multiplier: Double = 2.0,
    factor: Duration = 100.milliseconds,
    retries: Int = 10,
    offset: Duration = 10.milliseconds,
    body: suspend () -> R
): R {
    var caught: Throwable? = null
    repeat(retries) {
        try {
            return body()
        } catch (e: Throwable) {
            caught = e
            delay(offset + factor * multiplier.pow(it))
        }
    }
    throw caught!!
}

//TODO resource loading
val resourcePrefix: String = "/"

@Stable
fun ColorScheme.surfaceVariantColorAtElevation(
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return surfaceVariant
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return surfaceTint.copy(alpha = alpha).compositeOver(surfaceVariant)
}