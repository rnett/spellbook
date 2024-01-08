package com.rnett.spellbook.model.spell

import kotlinx.serialization.Serializable

@Serializable
enum class Save {
    Fortitude, Reflex, Will;
}