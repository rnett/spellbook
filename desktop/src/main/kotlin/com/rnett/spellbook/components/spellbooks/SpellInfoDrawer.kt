package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.rnett.spellbook.components.onEnter
import com.rnett.spellbook.components.onEscape
import com.rnett.spellbook.components.spell.SpellDisplay
import com.rnett.spellbook.spell.Spell


enum class SpellDrawerState {
    Header, Full, Closed;

    val next
        get() = when (this) {
            Header -> Full
            Full -> Closed
            Closed -> Header
        }

    val changeExpanded
        get() = when (this) {
            Header -> Full
            Full -> Header
            else -> Closed
        }
}

@Composable
fun SpellInfoDrawer(spell: Spell, state: SpellDrawerState, setState: (SpellDrawerState) -> Unit) {
    if (state == SpellDrawerState.Closed) return
    val focusRequester = remember { FocusRequester() }

    key(state) {
        SideEffect {
            if (state == SpellDrawerState.Closed)
                focusRequester.freeFocus()
            else
                focusRequester.requestFocus()
        }
    }

    Box(
        Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onEscape { setState(SpellDrawerState.Closed) }
            .onEnter { setState(state.next) }
    ) {
        SpellDisplay(spell, null, state == SpellDrawerState.Full) {
            setState(state.changeExpanded)
        }
    }
}