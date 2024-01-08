package com.rnett.spellbook.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import com.rnett.spellbook.ui.components.PageScaffold
import com.rnett.spellbook.ui.components.TopBar
import com.rnett.spellbook.ui.navigation.WithNavigator
import com.rnett.spellbook.ui.pages.Page
import com.rnett.spellbook.ui.pages.SearchPage
import com.rnett.spellbook.ui.pages.SpellbooksPage
import com.rnett.spellbook.ui.sidebar.CartSidebar
import com.rnett.spellbook.ui.sidebar.Sidebar

private val topBarPages: List<Page> = listOf(SpellbooksPage, SearchPage)
private val topBarSidebars: List<Sidebar> = listOf(CartSidebar)

@Composable
fun MainPage() {
    MaterialTheme(
        colors = darkColors(
            background = MainColors.outsideColor,
        )
    ) {
        WithNavigator(SpellbooksPage) {
            Surface(color = MainColors.outsideColor) {
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