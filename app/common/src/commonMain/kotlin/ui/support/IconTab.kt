package com.rnett.spellbook.ui.support

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab

interface IconTab : Tab {
    val padding: Dp
        get() = 20.dp
    val showText: Boolean get() = false
}