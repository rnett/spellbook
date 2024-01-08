package com.rnett.spellbook.ui.pages

import androidx.compose.material.Text
import androidx.compose.runtime.Composable

object SearchPage : Page {
    @Composable
    override fun body() {
        Text("Search page")
    }

    @Composable
    override fun topBarTab() {
        Text("Search")
    }
}