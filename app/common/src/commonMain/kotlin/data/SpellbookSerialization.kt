package com.rnett.spellbook.data

import com.rnett.spellbook.model.spellbook.Spellbook
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object SpellbookSerialization {

    private val json = Json {
        prettyPrint = true
    }

    fun write(spellbook: Spellbook): String {
        return json.encodeToString(spellbook)
    }

    fun tryRead(data: String): Spellbook? {
        try {
            return json.decodeFromString(data)
        } catch (e: IllegalArgumentException) {
            return null
        } catch (e: SerializationException) {
            return null
        }
    }
}