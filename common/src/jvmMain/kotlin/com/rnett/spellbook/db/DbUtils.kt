package com.rnett.spellbook.db

import com.rnett.spellbook.filter.Filter
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.SqlExpressionBuilder
import org.jetbrains.exposed.sql.compoundAnd
import org.jetbrains.exposed.sql.compoundOr
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.transactions.transactionScope
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

fun <T> Filter<T>.compound(condition: (T) -> Op<Boolean>): Op<Boolean> = when (this) {
    is Filter.And -> map(condition).compoundAnd()
    is Filter.Or -> map(condition).compoundOr()
    is Filter.OrAnd -> map { it.compound(condition) }.compoundOr()
}