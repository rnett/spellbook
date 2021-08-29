package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
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

//TODO does not handle things like summons well.  Do I want sidebar here, too?

@Composable
fun SpellInfoDrawer(spell: Spell, close: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    key(spell) {
        SideEffect {
            focusRequester.requestFocus()
        }
    }

    Box(
        Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onEscape { close() }
            .onEnter { close() }
            .clickable(remember { MutableInteractionSource() }, indication = null) { close() }
    ) {
        SpellDisplay(spell, null, true, close)
    }
}