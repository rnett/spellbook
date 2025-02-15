package com.rnett.spellbook.model.spell

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@JvmInline
@Serializable
value class SpellRef(val spellName: String) {
    override fun toString(): String {
        return spellName
    }
}