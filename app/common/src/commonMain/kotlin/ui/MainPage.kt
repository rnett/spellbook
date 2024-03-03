package com.rnett.spellbook.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.rnett.spellbook.ui.cart.CartSidebar
import com.rnett.spellbook.ui.components.PageScaffold
import com.rnett.spellbook.ui.components.TopBar
import com.rnett.spellbook.ui.pages.top.tabs.SearchTab
import com.rnett.spellbook.ui.pages.top.tabs.SpellbooksTab
import com.rnett.spellbook.ui.sidebar.LocalSidebarState
import com.rnett.spellbook.ui.sidebar.Sidebar
import com.rnett.spellbook.ui.theme.AppTheme

private val topBarPages: List<Tab> = listOf(SpellbooksTab, SearchTab)
private val topBarSidebars: List<Sidebar> = listOf(CartSidebar)

@Composable
fun MainPage() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TabNavigator(SpellbooksTab) {
                PageScaffold(
                    topBar = {
                        TopBar(topBarPages, topBarSidebars)
                    },
                    content = { CurrentTab() },
                    sidebar = LocalSidebarState.current.sidebar.value
                )
            }
        }
    }
}