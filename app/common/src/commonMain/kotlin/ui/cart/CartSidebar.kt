package com.rnett.spellbook.ui.cart

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
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
        val cart = LocalCart.current
        Surface(Modifier.fillMaxSize(), tonalElevation = 3.dp) {
            LazyColumn(Modifier.padding(20.dp).selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(cart.spells, { it.name }) { spell ->
                    Box(Modifier.animateItemPlacement()) {
                        val selected = cart.selected(spell)
                        SpellInfo(
                            spell,
                            Modifier.selectable(selected) { cart.toggleSelection(spell) }
                                .fillMaxWidth(),
                            selected = selected
                        )
                    }
                }
            }
        }
    }


    override val showInTopBar: Boolean = true

    @Composable
    override fun topBarIcon(isActive: Boolean) {
        Icon(if (isActive) Icons.Default.ShoppingCart else Icons.Outlined.ShoppingCart, "Cart")
    }
}