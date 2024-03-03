package com.rnett.spellbook.ui.components.spellbook

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rnett.spellbook.model.spellbook.Spellbook
import kotlinx.collections.immutable.toPersistentList

@Composable
fun Spellbook(spellbook: Spellbook, update: (Spellbook) -> Unit) {
    Column(Modifier.fillMaxWidth().padding(5.dp)) {
        Text(spellbook.name, fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic, fontSize = 2.sp)
        Spacer(Modifier.height(5.dp))
        LazyRow(Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
            itemsIndexed(spellbook.spellcastings, { _, it -> it.name }) { idx, it ->
                Spellcasting(it) {
                    update(
                        spellbook.copy(spellcastings = spellbook.spellcastings.toPersistentList().set(idx, it))
                    )
                }
            }
            item {
                IconButton({}) {
                    Icon(Icons.Default.Add, "Add spellcasting")
                }
            }
        }
    }
}