package com.rnett.spellbook.filter

import kotlinx.serialization.Serializable

@Serializable
sealed class Filter<T> {

    @Serializable
    data class And<T>(val filters: Set<T>) : Filter<T>(), Set<T> by filters {
        constructor(vararg filters: T) : this(filters.toSet())
    }

    @Serializable
    data class Or<T>(val filters: Set<T>) : Filter<T>(), Set<T> by filters {
        constructor(vararg filters: T) : this(filters.toSet())
    }

    @Serializable
    data class OrAnd<T>(val ands: Set<And<T>>) : Filter<T>(), Set<And<T>> by ands {
        constructor(vararg filters: And<T>) : this(filters.toSet())
    }
}