package com.rnett.spellbook.ui.pages.top.tabs

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions

object SpellbooksTab : Tab {
    @Composable
    override fun Content() {
        Text("Spellbooks page")
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(0u, "Spellbooks")
}