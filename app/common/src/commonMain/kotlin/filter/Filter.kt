package com.rnett.spellbook.filter

import com.rnett.spellbook.spell.Spell
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

enum class Operation {
    AND, OR;
}

inline fun <T> Collection<T>.reduce(op: Operation, onEmpty: Boolean = true, value: (T) -> Boolean) =
    if (this.isEmpty()) onEmpty else when (op) {
        Operation.AND -> all(value)
        Operation.OR -> any(value)
    }

inline fun Collection<Boolean>.reduce(op: Operation, onEmpty: Boolean = true) =
    if (this.isEmpty()) onEmpty else when (op) {
        Operation.AND -> all { it }
        Operation.OR -> any { it }
    }

inline fun Boolean.negateIf(negate: Boolean) = if (negate) !this else this

@Serializable
data class FilterClause<T>(val filters: Set<T> = emptySet(), val negate: Boolean = false) {
    operator fun plus(filter: T) = FilterClause(filters + filter, negate)
    operator fun minus(filter: T) = FilterClause(filters - filter, negate)
    operator fun not() = FilterClause(filters, !negate)

    @Transient
    val isEmpty = filters.isEmpty()
}

@Serializable
class Filter<T : SpellFilterPart> private constructor(
    val clauses: List<FilterClause<T>>,
    val outerOperation: Operation,
    val clauseOperation: Operation,
    val negate: Boolean,
) :
    SpellFilterPart {
    constructor(
        clauses: Iterable<FilterClause<T>>,
        clauseOperation: Operation,
        outerOperation: Operation,
        negate: Boolean,
    ) : this(
        clauses.filter { !it.isEmpty },
        clauseOperation,
        outerOperation,
        negate
    )

    fun copy(
        clauses: Iterable<FilterClause<T>> = this.clauses,
        clauseOperation: Operation = this.clauseOperation,
        outerOperation: Operation = this.outerOperation,
        negate: Boolean = this.negate,
    ) =
        Filter(clauses, outerOperation, clauseOperation, negate)

    operator fun not() = copy(negate = !negate)

    override fun matches(spell: Spell): Boolean {
        if (clauses.isEmpty())
            return true
        return clauses.reduce(outerOperation) {
            it.filters.reduce(clauseOperation) { it.matches(spell) }.negateIf(it.negate)
        }.negateIf(negate)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Filter<*>

        if (clauses != other.clauses) return false
        if (clauseOperation != other.clauseOperation) return false
        if (outerOperation != other.outerOperation) return false
        if (negate != other.negate) return false

        return true
    }

    override fun hashCode(): Int {
        var result = clauses.hashCode()
        result = 31 * result + clauseOperation.hashCode()
        result = 31 * result + outerOperation.hashCode()
        result = 31 * result + negate.hashCode()
        return result
    }

    override fun toString(): String {
        return "Filter(clauses=$clauses, clauseOperation=$clauseOperation, outerOperation=$outerOperation, negate=$negate)"
    }
}

inline infix fun Boolean?.matchesIfNonNull(other: Boolean): Boolean {
    if (this == null) return true
    return this == other
}