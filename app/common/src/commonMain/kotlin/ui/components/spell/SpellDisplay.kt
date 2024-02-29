package com.rnett.spellbook.ui.components.spell

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.SpellReference
import com.rnett.spellbook.ui.cart.CartControl

@Composable
fun SpellDisplay(spellReference: SpellReference, modifier: Modifier = Modifier, enableCart: Boolean = true) {

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier.padding(20.dp)) {
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