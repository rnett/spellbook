package com.rnett.spellbook.ui.components.spell

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.SpellReference
import com.rnett.spellbook.ui.cart.CartControl

@Composable
fun SpellInfo(spellReference: SpellReference, modifier: Modifier = Modifier, enableCart: Boolean = true) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(0.2.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier.padding(top = 3.dp, bottom = 3.dp, start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(spellReference.name)
            Spacer(Modifier.weight(1f))
            if (enableCart) {
                CartControl(spellReference)
            }
        }
    }
}