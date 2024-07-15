package com.rnett.spellbook.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.Snapshot
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.rnett.spellbook.data.SpellbookReference
import com.rnett.spellbook.ui.cart.CartSidebar
import com.rnett.spellbook.ui.components.PageScaffold
import com.rnett.spellbook.ui.components.TopBar
import com.rnett.spellbook.ui.pages.top.tabs.NewSpellbookTab
import com.rnett.spellbook.ui.pages.top.tabs.SearchTab
import com.rnett.spellbook.ui.pages.top.tabs.SpellbookTab
import com.rnett.spellbook.ui.sidebar.LocalSidebarState
import com.rnett.spellbook.ui.sidebar.Sidebar
import com.rnett.spellbook.ui.theme.AppTheme

@Stable
class MainPageModel(
    val prefixTabs: List<Tab>,
    val postfixTabs: List<Tab>,
    val navigator: TabNavigator,
    val sidebars: List<Sidebar> = listOf(CartSidebar)
) : ScreenModel {
    private val spellbookTabs = mutableStateListOf<SpellbookTab>()
    val tabs: List<Tab> by derivedStateOf { prefixTabs + spellbookTabs + postfixTabs }

    fun openOrSelectSpellbook(reference: SpellbookReference) {
        Snapshot.withMutableSnapshot {
            val index = spellbookTabs.indexOfFirst { it.reference == reference }
            if (index == -1) {
                val tab = SpellbookTab(reference)
                spellbookTabs += tab
                navigator.current = tab
            } else {
                navigator.current = spellbookTabs[index]
            }
        }
    }

    fun openSpellbook(reference: SpellbookReference) {
        Snapshot.withMutableSnapshot {
            val tab = spellbookTabs.firstOrNull { it.reference == reference }
            if (tab != null) {
                navigator.current = tab
            }
        }
    }

    fun closeSpellbook(reference: SpellbookReference) {
        Snapshot.withMutableSnapshot {
            val index = spellbookTabs.indexOfFirst { it.reference == reference }.takeIf { it >= 0 } ?: return
            val tab = spellbookTabs[index]
            if (navigator.current == tab) {
                if (spellbookTabs.size == 1) {
                    navigator.current = prefixTabs.firstOrNull() ?: postfixTabs.first()
                } else {
                    if (index == 0)
                        navigator.current = spellbookTabs[1]
                    else
                        navigator.current = spellbookTabs[index - 1]
                }
            }
            spellbookTabs.removeAt(index)
        }
    }

    fun openTab(tab: Tab) {
        navigator.current = tab
    }
}

@Composable
fun MainPage() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TabNavigator(NewSpellbookTab) {
                val nav = LocalTabNavigator.current
                val model = LocalNavigator.currentOrThrow.rememberNavigatorScreenModel {
                    MainPageModel(
                        listOf(SearchTab),
                        listOf(NewSpellbookTab),
                        nav
                    )
                }

                PageScaffold(
                    topBar = {
                        TopBar(model.tabs, model.sidebars) {
                            if (it is SpellbookTab) return@TopBar { model.closeSpellbook(it.reference) }
                            return@TopBar null
                        }
                    },
                    content = { CurrentTab() },
                    sidebar = LocalSidebarState.current.sidebar.value
                )
            }
        }
    }
}