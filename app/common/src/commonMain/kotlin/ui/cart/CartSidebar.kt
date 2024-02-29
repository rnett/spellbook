package com.rnett.spellbook.ui.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.ui.components.spell.SpellInfo
import com.rnett.spellbook.ui.sidebar.Sidebar


object CartSidebar : Sidebar {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun render() {
        val spells = LocalCart.current.spells
        Surface(Modifier.fillMaxSize(), tonalElevation = 3.dp) {
            LazyColumn(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(spells, { it.name }) {
                    Row(Modifier.animateItemPlacement()) {
                        SpellInfo(it, Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }


    override val showInTopBar: Boolean = true

    @Composable
    override fun topBarIcon() {
        Icon(Icons.Default.ShoppingCart, "Cart")
    }
}