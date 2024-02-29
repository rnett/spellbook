package com.rnett.spellbook.ui.pages

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

object SpellbooksPage : Page {
    @Composable
    override fun body() {
        Text("Spellbooks page")
    }

    @Composable
    override fun topBarTab() {
        Text("Spellbooks")
    }
}