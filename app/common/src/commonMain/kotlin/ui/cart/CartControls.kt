package com.rnett.spellbook.ui.cart

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.rnett.spellbook.model.spellbook.SpellReference

@Composable
fun CartIcon(inCart: Boolean) {
    if (inCart) {
        Icon(Icons.Default.RemoveShoppingCart, "Remove from cart")
    } else {
        Icon(Icons.Default.AddShoppingCart, "Add to cart")
    }
}

@Composable
fun CartButton(inCart: Boolean, onClick: () -> Unit) {
    IconButton(onClick) {
        CartIcon(inCart)
    }
}

@Composable
fun CartControl(spellReference: SpellReference) {
    val cart = LocalCart.current
    CartButton(spellReference in cart) {
        cart.addOrRemove(spellReference)
    }
}