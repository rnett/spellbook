package com.rnett.spellbook.components.spell

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.spell.Spell

@Composable
fun DraggingSpell(spell: Spell) {
    Row {
        Spacer(Modifier.width(15.dp))
        Surface(
            shape = RoundedCornerShape(4.dp),
            border = BorderStroke(1.dp, Color.Black),
            color = Color.DarkGray.copy(alpha = 0.8f).compositeOver(Color.LightGray),
            elevation = 20.dp
        ) {
            Row(Modifier.padding(12.dp, 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(spell.name)
            }
        }
    }
}