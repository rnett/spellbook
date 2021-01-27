package com.rnett.spellbook.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AmbientTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Providers
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rnett.spellbook.Color
import com.rnett.spellbook.asCompose

@Composable
fun SpellTag(content: String, color: Color, modifier: Modifier = Modifier, ) = SpellTag(color.asCompose(), modifier){ Text(content) }

@Composable
fun SpellTag(content: String, color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier, ) = SpellTag(color, modifier){ Text(content) }

@Composable
fun SpellTag(color: Color, modifier: Modifier = Modifier, content: @Composable () -> Unit) = SpellTag(color.asCompose(), modifier, content)

@Composable
fun SpellTag(color: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(shape = RoundedCornerShape(8.dp), color = color) {
        Box(modifier.padding(5.dp, 3.dp)) {
            Providers(AmbientTextStyle provides TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)) {
                content()
            }
        }
    }
}