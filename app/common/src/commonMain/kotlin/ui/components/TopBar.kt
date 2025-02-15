package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.ui.sidebar.LocalSidebarState
import com.rnett.spellbook.ui.sidebar.Sidebar

@Composable
fun TopBar(tabs: List<@Composable () -> Unit>, sidebars: List<Sidebar>) {
    val sidebarState = LocalSidebarState.current
    var currentSidebar by sidebarState.sidebar

    Surface(Modifier.fillMaxWidth().height(50.dp), tonalElevation = 0.5.dp) {
        Row {
            Row(Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.Start) {
                tabs.forEach {
                    it()
                }
                Spacer(Modifier.weight(1f))
            }
            Spacer(Modifier.weight(1f))
            Row(Modifier.fillMaxWidth(0.3f), horizontalArrangement = Arrangement.End) {
                sidebars.filter { it.showInTopBar }.forEach { sidebar ->
                    val active = currentSidebar == sidebar
                    SidebarIcon(sidebar, active) {
                        if (it)
                            currentSidebar = sidebar
                        else
                            currentSidebar = null
                    }
                }
            }
        }
    }
}

@Composable
private fun SidebarIcon(sidebar: Sidebar, isActive: Boolean, onClick: (Boolean) -> Unit) {
    IconButton({ onClick(!isActive) }) {
        sidebar.topBarIcon(isActive)
    }
}