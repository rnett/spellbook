package com.rnett.spellbook.ui.cart

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import com.rnett.spellbook.model.spellbook.SpellReference

data class Cart(val spells: SnapshotStateList<SpellReference> = mutableStateListOf()) {
    operator fun contains(spellReference: SpellReference) = spellReference in spells
    operator fun plusAssign(spellReference: SpellReference) {
        if (spellReference !in this)
            spells.add(spellReference)
    }

    operator fun minusAssign(spellReference: SpellReference) {
        spells.remove(spellReference)
    }

    fun toggle(spellReference: SpellReference) {
        if (spellReference in this)
            this -= spellReference
        else
            this += spellReference
    }
}

val LocalCart = staticCompositionLocalOf { Cart() }