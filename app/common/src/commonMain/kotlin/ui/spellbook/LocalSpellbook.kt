package com.rnett.spellbook.ui.spellbook

import androidx.compose.runtime.*
import com.rnett.spellbook.data.LoadedSpellbook

//TODO need a manager/viewmodel here, w/ saving

data class LocalSpellbookState(val state: MutableState<LoadedSpellbook?> = mutableStateOf(null)) {
    val spellbook get() = state.value?.spellbook

    fun set(loadedSpellbook: LoadedSpellbook) {
        this.state.value = loadedSpellbook
    }

    var loadedSpellbook by state

}

val LocalSpellbook = staticCompositionLocalOf { LocalSpellbookState() }