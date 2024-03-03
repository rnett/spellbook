package com.rnett.spellbook.ui.pages.top.tabs

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rnett.spellbook.ui.pages.spellbooks.EditScreen
import com.rnett.spellbook.ui.pages.spellbooks.NewScreen
import com.rnett.spellbook.ui.spellbook.LocalSpellbook

object SpellbooksTab : Tab {
    @Composable
    override fun Content() {
        Navigator(LocalSpellbook.current.loadedSpellbook?.let { EditScreen(it) } ?: NewScreen())
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(0u, "Spellbooks")
}