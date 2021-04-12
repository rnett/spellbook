package com.rnett.spellbook.db

import com.rnett.spellbook.filter.Operation
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.compoundOr
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


@OptIn(ExperimentalContracts::class)
inline fun <T> sqlExpression(block: SqlExpressionBuilder.() -> T): T {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return SqlExpressionBuilder.run(block)
}

fun List<Op<Boolean>>.compound(operation: Operation) = when (operation) {
    Operation.AND -> compoundAnd()
    Operation.OR -> compoundOr()
}

//fun <T> Filter<T>.compound(condition: (T) -> Op<Boolean>): Op<Boolean> {
//    val meaningfulClauses = clauses.filter { it.isNotEmpty() }
//    if(meaningfulClauses.isEmpty())
//        return Op.TRUE
//    return meaningfulClauses.map { it.map(condition).compound(clauseOperation) }.compound(outerOperation)
//}