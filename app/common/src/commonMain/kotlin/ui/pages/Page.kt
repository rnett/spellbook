package com.rnett.spellbook.ui.pages

import androidx.compose.runtime.Composable

interface Page {
    @Composable
    fun body()

    @Composable
    fun topBarTab()
}