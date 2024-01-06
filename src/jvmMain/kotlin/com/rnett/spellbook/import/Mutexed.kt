package com.rnett.spellbook.import

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class Mutexed<T>(@Deprecated("Use withLock") val value: T) {
    val mutex = Mutex()

    @OptIn(ExperimentalContracts::class)
    suspend inline fun withLock(block: (T) -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        mutex.withLock {
            block(value)
        }
    }
}