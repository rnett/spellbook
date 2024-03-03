package com.rnett.spellbook.ui.cart

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.rnett.spellbook.model.spellbook.SpellReference

@Stable
class Cart(
) {
    private val _spells: SnapshotStateList<SpellReference> = mutableStateListOf()
    private var _selectedSpell by mutableStateOf<SpellReference?>(null)

    val selectedSpell: SpellReference? get() = _selectedSpell

    operator fun contains(spellReference: SpellReference) = spellReference in _spells
    operator fun plusAssign(spellReference: SpellReference) {
        if (spellReference !in this)
            _spells.add(spellReference)
    }

    operator fun minusAssign(spellReference: SpellReference) {
        _spells.remove(spellReference)
    }

    fun select(spellReference: SpellReference) {
        if (spellReference in this)
            _selectedSpell = spellReference
    }

    fun deselect(spellReference: SpellReference) {
        if (spellReference in this)
            _selectedSpell = null
    }

    fun toggleSelection(spellReference: SpellReference) {
        if (spellReference in this) {
            _selectedSpell = if (selectedSpell == spellReference) null else spellReference

        }
    }

    fun addOrRemove(spellReference: SpellReference) {
        if (spellReference in this)
            this -= spellReference
        else
            this += spellReference
    }

    fun selected(spellReference: SpellReference): Boolean = selectedSpell == spellReference

    val spells: List<SpellReference> = _spells
}

val LocalCart = staticCompositionLocalOf { Cart() }