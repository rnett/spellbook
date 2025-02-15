package com.rnett.spellbook.ui.components.form

import com.rnett.spellbook.ui.components.form.Validated.Invalid
import com.rnett.spellbook.ui.components.form.Validated.Valid
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract


sealed interface Validated<out T> {
    data class Valid<out T>(val value: T) : Validated<T>
    data class Invalid(val error: String) : Validated<Nothing>

    val valueOrNull get() = if (this is Valid) value else null
    val errorOrNull get() = if (this is Invalid) error else null
}

@OptIn(ExperimentalContracts::class)
fun <T> Validated<T>.isValid(): Boolean {
    contract {
        returns(true) implies (this@isValid is Valid<T>)
        returns(false) implies (this@isValid is Invalid)
    }
    return this is Valid
}

@OptIn(ExperimentalContracts::class)
fun <T> Validated<T>.isInvalid(): Boolean {
    contract {
        returns(true) implies (this@isInvalid is Invalid)
        returns(false) implies (this@isInvalid is Valid<T>)
    }
    return this is Invalid
}

inline fun <T, R> Validated<T>.map(transform: (T) -> R): Validated<R> {
    return when (this) {
        is Invalid -> this
        is Valid -> Valid(transform(value))
    }
}

inline fun <T, R> Validated<T>.flatMap(transform: (T) -> Validated<R>): Validated<R> {
    return when (this) {
        is Invalid -> this
        is Valid -> transform(value)
    }
}

fun interface Validation<T, R> {
    fun validate(value: T): Validated<R>

    companion object {

        val NotBlank = Filter<String> {
            if (it.isEmpty()) return@Filter "Must not be empty"
            if (it.isBlank()) return@Filter "Must not be blank"
            null
        }
    }

    class None<T : R, R> : Validation<T, R> {
        override fun validate(value: T): Validated<R> = Valid(value)
    }

    data class Min<T : Comparable<T>>(
        private val minimum: () -> T?,
        private val exclusive: Boolean = true,
        private val message: () -> String = { "Must be ${if (exclusive) ">" else ">="} ${minimum()}" }
    ) : Validation<T, T> {
        override fun validate(value: T): Validated<T> {
            val min = minimum() ?: return Valid(value)
            if (exclusive) {
                if (value > min)
                    return Valid(value)
                return Invalid(message())
            } else {
                if (value >= min)
                    return Valid(value)
                return Invalid(message())
            }
        }
    }

    data class Max<T : Comparable<T>>(
        private val maximum: () -> T?,
        private val exclusive: Boolean = true,
        private val message: () -> String = { "Must be ${if (exclusive) ">" else ">="} ${maximum()}" }
    ) : Validation<T, T> {
        override fun validate(value: T): Validated<T> {
            val max = maximum() ?: return Valid(value)
            if (exclusive) {
                if (value < max)
                    return Valid(value)
                return Invalid(message())
            } else {
                if (value <= max)
                    return Valid(value)
                return Invalid(message())
            }
        }
    }

    data object ToInt : Validation<String, Int> {
        override fun validate(value: String): Validated<Int> =
            value.toIntOrNull()?.let(::Valid) ?: Invalid("Not an integer")
    }

    data object ToDouble : Validation<String, Double> {
        override fun validate(value: String): Validated<Double> =
            value.toDoubleOrNull()?.let(::Valid) ?: Invalid("Not an integer")
    }

    data object ToIntIfPresent : Validation<String, Int?> {
        override fun validate(value: String): Validated<Int?> {
            if (value.isEmpty()) return Valid(null)
            return value.toIntOrNull()?.let(::Valid) ?: Invalid("Not an integer")
        }
    }

    data class Filter<T>(val message: (T) -> String?) : Validation<T, T> {
        override fun validate(value: T): Validated<T> {
            val result = message(value) ?: return Valid(value)
            return Invalid(result)
        }
    }

    class NotNull<T>(val message: () -> String = { "Must be set" }) : Validation<T?, T> {
        override fun validate(value: T?): Validated<T> {
            return value?.let(::Valid) ?: Invalid(message())
        }
    }
}

fun <T, R> Validation<T, R>.ifPresent(): Validation<T?, R?> =
    Validation { if (it != null) this.validate(it) else Validated.Valid(null) }

inline fun <T, R, S> Validation<T, R>.then(next: Validation<R, S>) =
    Validation<T, S> { this.validate(it).flatMap { next.validate(it) } }

inline operator fun <T, R, S> Validation<T, R>.plus(next: Validation<R, S>) = this.then(next)
