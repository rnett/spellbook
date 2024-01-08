package com.rnett.spellbook.ui.sidebar

import androidx.compose.runtime.Composable

interface Sidebar {
    @Composable
    fun render()

    val showInTopBar: Boolean get() = false
    @Composable
    fun topBarIcon() {

    }

    val width: Float
        get() = defaultWidth

    companion object {
        const val defaultWidth: Float = 0.15f
    }
}