package com.rnett.spellbook.ui.pages.top.tabs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rnett.spellbook.model.spellbook.SpellReference
import com.rnett.spellbook.ui.components.spell.SpellDisplay

object SearchTab : Tab {

    private val spells = (0..10).map { SpellReference("Test $it") }

    @Composable
    override fun Content() {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            items(spells) {
                SpellDisplay(it, Modifier.fillMaxWidth())
            }
        }
    }

    override val options: TabOptions
        @Composable
        get() = TabOptions(1u, "Search")
}