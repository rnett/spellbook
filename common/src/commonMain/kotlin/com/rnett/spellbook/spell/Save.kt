package com.rnett.spellbook.spell

import kotlinx.serialization.Serializable

@Serializable
enum class Save {
    Fortitude, Reflex, Will;
}