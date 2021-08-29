package com.rnett.spellbook.components.sidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.rnett.spellbook.ShoppingCart

//TODO implement
@Composable
fun ShoppingCartDisplay(cart: ShoppingCart) {
    Column {
        cart.forEach {
            Text(it.name)
            //TODO extract short spell display/header from spellbooks, use here too?
        }
    }
}