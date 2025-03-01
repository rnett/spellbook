package com.rnett.spellbook.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.rnett.spellbook.ui.cart.CartSidebar
import com.rnett.spellbook.ui.components.NavTab
import com.rnett.spellbook.ui.components.PageScaffold
import com.rnett.spellbook.ui.components.TopBar
import com.rnett.spellbook.ui.pages.search.SearchNav
import com.rnett.spellbook.ui.pages.search.SearchRoutes
import com.rnett.spellbook.ui.pages.spellbook.SpellbookNav
import com.rnett.spellbook.ui.pages.spellbook.SpellbookRoutes
import com.rnett.spellbook.ui.sidebar.LocalSidebarState
import com.rnett.spellbook.ui.theme.AppTheme
import kotlinx.serialization.Serializable

data object TabsNav {
    @Serializable
    data object Spellbook

    @Serializable
    data object Search
}

@Composable
fun MainPage() {

    val navController = rememberNavController()

    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PageScaffold(
                topBar = {
                    TopBar(
                        listOf(
                            {
                                NavTab(navController, TabsNav.Spellbook, "Spellbook")
                            },
                            {
                                NavTab(navController, TabsNav.Search, "Search", Icons.Default.Search)
                            }
                        ),
                        listOf(CartSidebar)
                    )
                },
                content = {
                    NavHost(navController, TabsNav.Spellbook) {
                        navigation<TabsNav.Spellbook>(SpellbookRoutes.OpenOrNew) {
                            SpellbookNav(navController)
                        }
                        navigation<TabsNav.Search>(SearchRoutes.Search("")) {
                            SearchNav(navController)
                        }
                    }
                },
                sidebar = LocalSidebarState.current.sidebar.value
            )
        }
    }
}