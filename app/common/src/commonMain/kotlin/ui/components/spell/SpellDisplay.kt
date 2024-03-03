package com.rnett.spellbook.ui.components.spell

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.SpellReference
import com.rnett.spellbook.ui.cart.CartControl

@Composable
fun SpellDisplay(spellReference: SpellReference, modifier: Modifier = Modifier, enableCart: Boolean = true) {
    OutlinedCard(elevation = CardDefaults.elevatedCardElevation(10.dp)) {
        Column(modifier.padding(vertical = 10.dp, horizontal = 20.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Text(spellReference.name)
                Spacer(Modifier.weight(1f))
                if (enableCart) {
                    CartControl(spellReference)
                }
            }
            Text(
                """
                Some description
                
                Blah blah blah
            """.trimIndent()
            )
        }
    }
}