package com.rnett.spellbook

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

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

expect val resourcePrefix: String