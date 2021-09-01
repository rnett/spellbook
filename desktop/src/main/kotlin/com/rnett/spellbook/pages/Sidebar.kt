package com.rnett.spellbook.pages

import androidx.compose.runtime.Composable
import com.rnett.spellbook.components.InfoSidebarData
import com.rnett.spellbook.components.InfoSidebarState
import com.rnett.spellbook.components.SidebarInfoDisplay
import com.rnett.spellbook.components.sidebar.ShoppingCartDisplay


sealed class SidebarState {
    data class Info(val data: InfoSidebarData<*>, val sidebarState: InfoSidebarState) : SidebarState()
    data class ShoppingCart(val cart: com.rnett.spellbook.ShoppingCart) : SidebarState()
}

//TODO use.  going to want to make spell search in page form spellbooks
@Composable
fun Sidebar(state: SidebarState) {
    when (state) {
        is SidebarState.Info -> {
            SidebarInfoDisplay(state.data, state.sidebarState)
        }
        is SidebarState.ShoppingCart -> {
            ShoppingCartDisplay(state.cart)
        }
    }
}