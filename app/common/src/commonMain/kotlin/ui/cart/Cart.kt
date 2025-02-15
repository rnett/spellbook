package com.rnett.spellbook.ui.cart

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.rnett.spellbook.model.spell.SpellRef

@Stable
class Cart(
) {
    private val _spells: SnapshotStateList<SpellRef> = mutableStateListOf()
    private var _selectedSpell by mutableStateOf<SpellRef?>(null)

    val selectedSpell: SpellRef? get() = _selectedSpell

    operator fun contains(spellRef: SpellRef) = spellRef in _spells
    operator fun plusAssign(spellRef: SpellRef) {
        if (spellRef !in this)
            _spells.add(spellRef)
    }

    operator fun minusAssign(spellRef: SpellRef) {
        _spells.remove(spellRef)
    }

    fun select(spellRef: SpellRef) {
        if (spellRef in this)
            _selectedSpell = spellRef
    }

    fun deselect(spellRef: SpellRef) {
        if (spellRef in this)
            _selectedSpell = null
    }

    fun toggleSelection(spellRef: SpellRef) {
        if (spellRef in this) {
            _selectedSpell = if (selectedSpell == spellRef) null else spellRef

        }
    }

    fun addOrRemove(spellRef: SpellRef) {
        if (spellRef in this)
            this -= spellRef
        else
            this += spellRef
    }

    fun selected(spellRef: SpellRef): Boolean = selectedSpell == spellRef

    val spells: List<SpellRef> = _spells
}

val LocalCart = staticCompositionLocalOf { Cart() }