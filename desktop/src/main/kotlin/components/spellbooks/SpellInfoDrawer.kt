package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import com.rnett.spellbook.components.clickableNoIndication
import com.rnett.spellbook.components.onEnter
import com.rnett.spellbook.components.onEscape
import com.rnett.spellbook.components.spell.SpellDisplay
import com.rnett.spellbook.spell.Spell

//TODO does not handle things like summons well.  Do I want sidebar here, too?

@Composable
fun SpellInfoDrawer(spell: Spell, close: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(spell) {
        focusRequester.requestFocus()
    }

    Box(
        Modifier
            .focusRequester(focusRequester)
            .focusable()
            .onEscape { close() }
            .onEnter { close() }
            .clickableNoIndication { close() }
    ) {
        SpellDisplay(spell, null, true, close)
    }
}