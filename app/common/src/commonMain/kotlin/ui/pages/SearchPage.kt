package com.rnett.spellbook.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.SpellReference
import com.rnett.spellbook.ui.components.spell.SpellDisplay

object SearchPage : Page {

    private val spells = (0..10).map { SpellReference("Test $it") }

    @Composable
    override fun body() {
        LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            items(spells) {
                SpellDisplay(it, Modifier.fillMaxWidth())
            }
        }
    }

    @Composable
    override fun topBarTab() {
        Text("Search")
    }
}