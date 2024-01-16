package com.rnett.spellbook.extractor

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject

object Resources {
    fun readText(resource: String): String {
        return Resources::class.java.classLoader.getResourceAsStream(resource)?.use {
            it.reader().readText()
        } ?: error("Resource file $resource not found")
    }
}

inline fun JsonObject.copy(builder: JsonObjectBuilder.() -> Unit): JsonObject = buildJsonObject {
    this@copy.forEach { (k, v) -> this.put(k, v) }
    builder()
}

object Serialization {
    val Lenient = Json {
        isLenient = true
        coerceInputValues = true
        decodeEnumsCaseInsensitive = true
        allowTrailingComma = true
    }
    val IgnoreUnknown = Json {
        ignoreUnknownKeys = true
        isLenient = true
        coerceInputValues = true
        decodeEnumsCaseInsensitive = true
        allowTrailingComma = true
    }
    val Normal = Json {
        decodeEnumsCaseInsensitive = true
        allowTrailingComma = true
    }
    val Pretty = Json {
        decodeEnumsCaseInsensitive = true
        allowTrailingComma = true
        prettyPrint = true
    }

}