package com.rnett.spellbook.extractor.aon.raw

import com.rnett.spellbook.extractor.Serialization
import com.rnett.spellbook.extractor.aon.aonClient
import com.rnett.spellbook.extractor.aon.processed.Processing
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlin.reflect.KProperty1
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.typeOf

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.PROPERTY_GETTER)
annotation class EnumLike

object AonSpellAnalysis {
    fun <H, T : Any> getFieldValues(items: List<H>, field: KProperty1<H, T?>): Set<T> =
        items.mapNotNullTo(mutableSetOf()) { field.get(it) }

    @Suppress("UNCHECKED_CAST")
    fun <H, T> getListFieldValues(items: List<H>, field: KProperty1<H, Iterable<T?>?>): Set<T> =
        items.flatMapTo(mutableSetOf()) { field.get(it)?.filterNot { it != null }.orEmpty() as List<T> }

    @Suppress("UNCHECKED_CAST")
    inline fun <reified H> analyze(items: List<H>): Map<KProperty1<H, *>, Set<*>> {
        val members = H::class.members
        val fields =
            members.filterIsInstance<KProperty1<H, *>>().filter {
                it.getter.hasAnnotation<EnumLike>()
            }
        return fields.associateWith {
            if (it.returnType.isSubtypeOf(typeOf<Iterable<*>?>()))
                getListFieldValues(items, it as KProperty1<H, Iterable<*>>)
            else
                getFieldValues(items, it)
        }
    }

    inline fun <reified H> report(
        spells: List<H>, printValues: (Iterable<Any?>) -> Unit = {
            it.forEach {
                println("\t$it")
            }
        }
    ) {
        val analysis = analyze(spells)
        analysis.forEach { (property, values) ->
            println("${H::class.simpleName}.${property.name}:")
            printValues(values)
            println()
        }
    }

    fun printProcessorSkeleton(values: Iterable<Any?>) {
        val processing =
            Processing(emptyMap(), values.associate { it.toString().lowercase() to JsonObject(emptyMap()) })
        println(Serialization.Pretty.encodeToString(processing))
    }

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
        val spells = AonTableSpell.loadAllSpells(aonClient, 50).toList()

        report(spells, ::printProcessorSkeleton)
    }
}