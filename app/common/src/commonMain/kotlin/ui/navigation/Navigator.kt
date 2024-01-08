package com.rnett.spellbook.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.Snapshot
import com.rnett.spellbook.ui.pages.Page
import com.rnett.spellbook.ui.sidebar.Sidebar

class Navigator(private val state: MutableState<NavigationState>) {
    fun gotoPage(page: Page) {
        Snapshot.withMutableSnapshot {
            state.value = state.value.copy(page = page)
        }
    }

    fun setSidebar(sidebar: Sidebar?) {
        Snapshot.withMutableSnapshot {
            state.value = state.value.copy(sidebar = sidebar)
        }
    }

    val page by derivedStateOf { state.value.page }
    val sidebar by derivedStateOf { state.value.sidebar }

    companion object {
        @Composable
        fun currentPage(): Page = LocalNavigator.current.page

        @Composable
        fun currentSidebar(): Sidebar? = LocalNavigator.current.sidebar
    }
}

@Composable
fun WithNavigator(initialPage: Page, content: @Composable (Navigator) -> Unit) {
    val nav = remember { Navigator(mutableStateOf(NavigationState(initialPage, null))) }
    CompositionLocalProvider(LocalNavigator provides nav) {
        content(LocalNavigator.current)
    }
}

val LocalNavigator = staticCompositionLocalOf<Navigator> { error("No navigator bound") }