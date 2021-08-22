package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.components.join
import com.rnett.spellbook.components.spell.SpellListTag
import com.rnett.spellbook.spellbook.Spellcasting

@Composable
fun SpellcastingHeader(name: String, spellcasting: Spellcasting<*>) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, fontWeight = FontWeight.Bold, fontSize = 1.5.em)

        Text(spellcasting.type.name, fontSize = 1.2.em)

        Row {
            spellcasting.defaultLists.join({ Spacer(Modifier.width(1.dp)) }) {
                SpellListTag(it)
            }
        }

        Text(spellcasting.maxLevel.toString(), fontWeight = FontWeight.Bold)
    }
}