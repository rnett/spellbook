package com.rnett.spellbook.ui.sidebar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf

data class SidebarState(val sidebar: MutableState<Sidebar?> = mutableStateOf(null))

val LocalSidebarState = staticCompositionLocalOf { SidebarState() }

interface Sidebar {
    @Composable
    fun render()

    val showInTopBar: Boolean get() = false
    @Composable
    fun topBarIcon(isActive: Boolean) {

    }

    val width: Float
        get() = defaultWidth

    companion object {
        const val defaultWidth: Float = 0.15f
    }
}