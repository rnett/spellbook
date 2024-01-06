package com.rnett.spellbook.extractor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeToSequence


private val Json = Json {
    this.isLenient = true
    this.coerceInputValues = true
    this.decodeEnumsCaseInsensitive = true
    this.allowTrailingComma = true
}

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    kotlinx.serialization.json.Json.decodeToSequence<Spell>(System.`in`).forEach {
        println(it)
    }
}