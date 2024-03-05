package com.rnett.spellbook.ui.components.spell

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.SpellReference
import com.rnett.spellbook.ui.cart.CartControl
import com.rnett.spellbook.utils.surfaceVariantColorAtElevation


@Composable
fun SpellInfoCard(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    content: @Composable RowScope.() -> Unit,
) {
    OutlinedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = if (selected) 20.dp else 0.dp),
        colors = CardDefaults.outlinedCardColors(MaterialTheme.colorScheme.surfaceVariantColorAtElevation(if (selected) 20.dp else 0.dp)),
        border = BorderStroke(if (selected) 1.dp else 0.2.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier.padding(top = 3.dp, bottom = 3.dp, start = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            content()
        }
    }
}

@Composable
fun SpellInfo(
    spellReference: SpellReference,
    modifier: Modifier = Modifier,
    enableCart: Boolean = true,
    selected: Boolean = false,
    prefix: @Composable () -> Unit = {},
    suffix: @Composable () -> Unit = {},
) {
    SpellInfoCard(modifier, selected) {
        prefix.invoke()
        Text(spellReference.name)
        Spacer(Modifier.weight(1f))
        if (enableCart) {
            CartControl(spellReference)
        }
        suffix.invoke()
    }
}