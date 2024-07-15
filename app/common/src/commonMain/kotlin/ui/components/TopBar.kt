package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.rnett.spellbook.ui.sidebar.LocalSidebarState
import com.rnett.spellbook.ui.sidebar.Sidebar
import com.rnett.spellbook.ui.support.IconTab

@Composable
fun TopBar(tabs: List<Tab>, sidebars: List<Sidebar>, closable: (Tab) -> (() -> Unit)?) {
    val nav = LocalTabNavigator.current
    val sidebarState = LocalSidebarState.current
    var currentSidebar by sidebarState.sidebar

    Surface(Modifier.fillMaxWidth().height(50.dp), tonalElevation = 0.5.dp) {
        Row {
            Row(Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.Start) {
                tabs.forEach {
                    val active = nav.current == it
                    val closer = closable(it)
                    PageTab(it, active, closer) {
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
private fun RowScope.PageTab(tab: Tab, isActive: Boolean, closer: (() -> Unit)?, onClick: () -> Unit) {
    Surface(tonalElevation = if (isActive) 10.dp else 0.dp) {
        Tab(
            isActive,
            onClick,
            Modifier.width(IntrinsicSize.Min),
            text = if (tab is IconTab && !tab.showText) null else {
                {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(tab.options.title)
                        if (closer != null) {
                            Spacer(Modifier.width(16.dp))
                            IconButton({ closer.invoke() }, Modifier.size(16.dp)) {
                                Icon(Icons.Default.Close, "Close")
                            }
                        }
                    }
                }
            },
            icon = tab.options.icon?.let {
                {
                    val modifier = if (tab is IconTab) Modifier.padding(horizontal = tab.padding) else Modifier
                    Icon(it, tab.options.title, modifier)
                }
            }
        )
    }
}

@Composable
private fun SidebarIcon(sidebar: Sidebar, isActive: Boolean, onClick: (Boolean) -> Unit) {
    IconButton({ onClick(!isActive) }) {
        sidebar.topBarIcon(isActive)
    }
}