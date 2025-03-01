package com.rnett.spellbook.ui.pages.spellbook

import androidx.compose.runtime.Composable
import com.rnett.spellbook.data.SpellbookReference
import com.rnett.spellbook.ui.components.spellbook.SpellbookLoaderAndCreator

@Composable
fun OpenOrNewSpellbookPage(loadSpellbook: (SpellbookReference) -> Unit) {
    SpellbookLoaderAndCreator({ loadSpellbook(it) }, { loadSpellbook(it) })
}