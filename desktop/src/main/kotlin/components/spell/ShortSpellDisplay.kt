package com.rnett.spellbook.components.spell

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
fun ShortSpellDisplay(
    spell: Spell,
    modifier: Modifier = Modifier,
    showLists: Boolean = true,
    showLevel: Boolean = false
) {
    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showLevel) {
            Row(Modifier.padding(horizontal = 8.dp)) {
                Text(spell.level.toString())
            }
        }
        Row(Modifier.widthIn(200.dp, 300.dp).fillMaxWidth(0.5f)) {
            Text(spell.name)
        }

        Spacer(Modifier.width(20.dp))

        Row(
            Modifier.height(20.dp).widthIn(min = 30.dp).fillMaxWidth(0.1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionsTag(spell.actions)
        }

        if (showLists) {
            Spacer(Modifier.width(20.dp))

            FlowRow(Modifier.widthIn(min = 50.dp), horizontalGap = 10.dp) {
                spell.lists.forEach {
                    SpellListShortTag(it)
                }
            }
        }
    }
}