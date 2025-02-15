package com.rnett.spellbook.ui.components.form

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable

@Stable
interface FormValue<R> {
    val validated: Validated<R>

    val hasError get() = validated.isInvalid()
    val isValid get() = validated.isValid()
}

@Stable
interface FormField<T, R> : FormValue<R> {
    fun update(value: T)
    var underlying: T
    val underlyingState: MutableState<T>
    override val validated: Validated<R>

    override val hasError get() = validated.isInvalid()
    override val isValid get() = validated.isValid()
}