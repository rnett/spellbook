package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.rnett.spellbook.ui.sidebar.LocalSidebarState
import com.rnett.spellbook.ui.sidebar.Sidebar

@Composable
fun TopBar(tabs: List<Tab>, sidebars: List<Sidebar>) {
    val nav = LocalTabNavigator.current
    val sidebarState = LocalSidebarState.current
    var currentSidebar by sidebarState.sidebar

    Surface(Modifier.fillMaxWidth().height(50.dp), tonalElevation = 0.5.dp) {
        Row {
            Row(Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.Start) {
                tabs.forEach {
                    val active = nav.current == it
                    PageTab(it, active) {
                        if (!active)
                            nav.current = it
                    }
                }
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
private fun RowScope.PageTab(tab: Tab, isActive: Boolean, onClick: () -> Unit) {
    Surface(tonalElevation = if (isActive) 10.dp else 0.dp) {
        Tab(
            isActive,
            onClick,
            Modifier.width(IntrinsicSize.Min),
            text = {
                Text(tab.options.title)
            },
            icon = tab.options.icon?.let { { Icon(it, tab.options.title) } }
        )
    }
}

@Composable
private fun SidebarIcon(sidebar: Sidebar, isActive: Boolean, onClick: (Boolean) -> Unit) {
    IconButton({ onClick(!isActive) }) {
        sidebar.topBarIcon(isActive)
    }
}