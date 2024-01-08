package com.rnett.spellbook.ui.sidebar

import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

object CartSidebar : Sidebar {
    @Composable
    override fun render() {
        Text("Cart todo")
    }


    override val showInTopBar: Boolean = true

    @Composable
    override fun topBarIcon() {
        Icon(Icons.Default.ShoppingCart, "Cart")
    }
}