package com.rnett.spellbook.extractor.aon.processed

import com.rnett.spellbook.extractor.Resources
import com.rnett.spellbook.extractor.Serialization
import kotlinx.serialization.Serializable

@Serializable
data class Processing<T>(val specific: Map<Int, T>, val general: Map<String, T>)

class Processor<T>(
    private val type: String,
    private val processing: Processing<T>,
    private val fallback: ((String) -> T?)?
) {
    fun process(value: String, id: Int? = null): T {
        if (id in processing.specific)
            return processing.specific[id]!!

        val valueKey = value.lowercase()
        if (valueKey in processing.general)
            return processing.general[valueKey]!!

        return fallback?.let { it(value) } ?: error("Could not process value for $type, from id $id: $value")
    }
}

object EnumLikeProcessor {

    inline fun <reified T> loadProcessing(): Processing<T> {
        val name = T::class.java.canonicalName
        val processesFile = Resources.readText("processing/$name.json")
        return Serialization.Normal.decodeFromString<Processing<T>>(processesFile)
    }

    inline fun <reified T> processor(noinline fallback: ((String) -> T?)? = null): Processor<T> {
        return Processor(
            T::class.qualifiedName!!,
            loadProcessing(),
            fallback
        )
    }

    inline fun <reified T> process(
        stringValue: String,
        spellId: Int? = null,
        noinline fallback: ((String) -> T?)? = null
    ): T {
        return processor(fallback).process(stringValue, spellId)
    }

    inline fun <reified T> batchProcess(
        values: Map<Int, String>,
        noinline fallback: ((String) -> T?)? = null
    ): Map<Int, T> {
        val processor = processor(fallback)
        return values.mapValues { processor.process(it.value, it.key) }
    }
}