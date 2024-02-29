package com.rnett.spellbook.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.rnett.spellbook.ui.cart.CartSidebar
import com.rnett.spellbook.ui.components.PageScaffold
import com.rnett.spellbook.ui.components.TopBar
import com.rnett.spellbook.ui.navigation.WithNavigator
import com.rnett.spellbook.ui.pages.Page
import com.rnett.spellbook.ui.pages.SearchPage
import com.rnett.spellbook.ui.pages.SpellbooksPage
import com.rnett.spellbook.ui.sidebar.Sidebar
import com.rnett.spellbook.ui.theme.AppTheme

private val topBarPages: List<Page> = listOf(SpellbooksPage, SearchPage)
private val topBarSidebars: List<Sidebar> = listOf(CartSidebar)

@Composable
fun MainPage() {
    AppTheme {
        WithNavigator(SpellbooksPage) {
            Surface(color = MaterialTheme.colorScheme.background) {
                PageScaffold(
                    topBar = {
                        TopBar(topBarPages, topBarSidebars)
                    },
                    page = it.page,
                    sidebar = it.sidebar
                )
            }
        }
    }
}