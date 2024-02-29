package com.rnett.spellbook.components.sidebar

import androidx.compose.foundation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.outlined.AddShoppingCart
import androidx.compose.material3.icons.outlined.RemoveShoppingCart
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.LocalMainState
import com.rnett.spellbook.ShoppingCart
import com.rnett.spellbook.components.*
import com.rnett.spellbook.components.spell.ShortSpellDisplay
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.pages.LightSidebarDivider

//TODO use inline content elsewhere: https://stackoverflow.com/questions/67605986/add-icon-at-last-word-of-text-in-jetpack-compose

@Composable
fun ShoppingCartDisplay(cart: ShoppingCart, close: () -> Unit) {

    var isDraggingOver by remember { mutableStateOf(false) }

    val dragTo = LocalMainState.current.dragSpellsToSide
    val dragFrom = LocalMainState.current.dragSpellsFromSide

    SidebarSurface(
        {
            Row(Modifier.padding(vertical = 10.dp)) {
                Text("Spell Cart", fontWeight = FontWeight.Bold)
            }
        },
        close,
        modifier = Modifier.draggableContainer(
            dragTo,
            onEnter = {
                isDraggingOver = true
            },
            onLeave = {
                isDraggingOver = false
            },
            accepts = { it !in cart },
            onDrop = {
                isDraggingOver = false
                cart.add(it)
                true
            }
        )
    ) {
        Row(Modifier.fillMaxSize()
            .ifLet(isDraggingOver) {
                it.background(Color.White.copy(alpha = 0.1f))
            }
        ) {
            if (cart.isEmpty()) {
                val text = buildAnnotatedString {
                    append("Cart is empty!  Add spells by dragging here from the search page, or clicking the ")
                    appendInlineContent("addToCart", "[Add to shopping cart]")
                    append(" icon in the spell header.")
                }

                Text(text, Modifier.padding(horizontal = 10.dp), inlineContent = mapOf(
                    "addToCart" to InlineTextContent(Placeholder(1.em, 1.em, PlaceholderVerticalAlign.TextCenter)) {
                        Icon(Icons.Outlined.AddShoppingCart, "Add to shopping cart")
                    }
                ))
            } else {
                val scrollState = rememberScrollState()
                Column(
                    Modifier.weight(1f)
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                        .padding(start = 10.dp)
                ) {
                    cart.join({
                        LightSidebarDivider()
                    }) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .draggableItem(dragFrom, it),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ShortSpellDisplay(it, Modifier.fillMaxWidth(0.9f), showLevel = true)
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
                            Spacer(Modifier.weight(0.1f).widthIn(max = 10.dp))
                        }
                    }
                }
                val scrollStyle =
                    LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
                VerticalScrollbar(
                    rememberScrollbarAdapter(scrollState),
                    style = scrollStyle
                )
            }
        }
    }
}