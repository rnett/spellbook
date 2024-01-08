package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Tab
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.ui.navigation.LocalNavigator
import com.rnett.spellbook.ui.pages.Page
import com.rnett.spellbook.ui.sidebar.Sidebar

@Composable
fun TopBar(pages: List<Page>, sidebars: List<Sidebar>) {
    val nav = LocalNavigator.current
    Row(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(0.7f), horizontalArrangement = Arrangement.Start) {
            pages.forEach {
                val active = nav.page::class == it::class
                PageTab(it, active) {
                    if (!active)
                        nav.gotoPage(it)
                }
            }
        }
        Spacer(Modifier.weight(1f))
        Row(Modifier.fillMaxWidth(0.3f), horizontalArrangement = Arrangement.End) {
            sidebars.filter { it.showInTopBar }.forEach { sidebar ->
                val active = nav.sidebar?.let { it::class == sidebar::class } ?: false
                SidebarIcon(sidebar, active) {
                    if (it)
                        nav.setSidebar(sidebar)
                    else
                        nav.setSidebar(null)
                }
            }
        }
    }
}

@Composable
private fun RowScope.PageTab(page: Page, isActive: Boolean, onClick: () -> Unit) {
    Tab(
        isActive,
        onClick,
        Modifier.padding(20.dp, 10.dp).width(IntrinsicSize.Min)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            page.topBarTab()
        }
    }
}

@Composable
private fun SidebarIcon(sidebar: Sidebar, isActive: Boolean, onClick: (Boolean) -> Unit) {
    IconToggleButton(isActive, onClick) {
        sidebar.topBarIcon()
    }
}