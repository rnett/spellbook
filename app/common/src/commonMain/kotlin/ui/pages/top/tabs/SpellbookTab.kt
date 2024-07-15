package com.rnett.spellbook.ui.pages.top.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rnett.spellbook.data.SpellbookReference
import com.rnett.spellbook.ui.pages.spellbooks.EditSpellbook

@Stable
data class SpellbookTab(val reference: SpellbookReference) : Tab {
    @Composable
    override fun Content() {
        EditSpellbook(reference)
    }

    override val options: TabOptions @Composable get() = TabOptions(0u, reference.name)
}