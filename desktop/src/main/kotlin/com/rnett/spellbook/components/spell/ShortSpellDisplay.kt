package com.rnett.spellbook.components.spell

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.components.core.FlowRow
import com.rnett.spellbook.spell.Spell

@Composable
fun ShortSpellDisplay(spell: Spell, modifier: Modifier = Modifier) {
    Row(
        modifier
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(Modifier.weight(0.2f).widthIn(min = 200.dp)) {
            Text(spell.name)
        }

        Spacer(Modifier.width(20.dp))

        Row(
            Modifier.height(20.dp).widthIn(min = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionsTag(spell.actions)
        }

        Spacer(Modifier.width(20.dp))

        FlowRow(Modifier.widthIn(min = 50.dp).weight(0.1f), horizontalGap = 10.dp) {
            spell.lists.forEach {
                SpellListShortTag(it)
            }
        }
    }
}