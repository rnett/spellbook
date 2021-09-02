package com.rnett.spellbook.components.sidebar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddShoppingCart
import androidx.compose.material.icons.outlined.RemoveShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.ShoppingCart
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.spell.ShortSpellDisplay

//TODO use inline content elsewhere: https://stackoverflow.com/questions/67605986/add-icon-at-last-word-of-text-in-jetpack-compose

//TODO finish

//TODO drag in and drag out
@Composable
fun ShoppingCartDisplay(cart: ShoppingCart, close: () -> Unit) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(cart) {
        focusRequester.requestFocus()
    }

    SidebarSurface({
        Row(Modifier.padding(vertical = 10.dp)) {
            Text("Spell Cart", fontWeight = FontWeight.Bold)
        }
    }, close, focusRequester = focusRequester) {
        if (cart.isEmpty()) {
            val text = buildAnnotatedString {
                append("Cart is empty!  Add spells by dragging here from the search page, or clicking the ")
                appendInlineContent("addToCart", "[Add to shopping cart]")
                append(" icon in the spell header.")
            }

            Text(text, inlineContent = mapOf(
                "addToCart" to InlineTextContent(Placeholder(1.em, 1.em, PlaceholderVerticalAlign.TextCenter)) {
                    Icon(Icons.Outlined.AddShoppingCart, "Add to shopping cart")
                }
            ))
        } else {
            Column(Modifier.fillMaxSize()) {
                cart.forEach {
                    Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        ShortSpellDisplay(it, Modifier.fillMaxWidth(0.8f))
                        Spacer(Modifier.weight(1f))
                        IconButtonHand(
                            {
                                cart.remove(it)
                            },
                            Modifier.height(24.dp)
                        ) {
                            IconWithTooltip(
                                Icons.Outlined.RemoveShoppingCart,
                                "Remove from Cart",
                                tint = Color.Red.copy(alpha = 0.6f)
                            )
                        }
                        Spacer(Modifier.weight(1f))
                    }
                    //TODO extract short spell display/header from spellbooks, use here too?
                }
            }
        }
    }
}