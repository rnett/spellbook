package com.rnett.spellbook

import kotlinx.serialization.Serializable

@Serializable
enum class Save {
    Fortitude, Reflex, Will;
}