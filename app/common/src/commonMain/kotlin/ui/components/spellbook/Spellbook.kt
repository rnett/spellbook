package com.rnett.spellbook.ui.components.spellbook

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.Spellbook
import kotlinx.collections.immutable.toPersistentList

@Composable
fun Spellbook(
    spellbook: Spellbook,
    onAddSpellcasting: () -> Unit,
    update: (Spellbook) -> Unit,
    buttons: (@Composable () -> Unit)? = null
) {
    Column(Modifier.padding(20.dp)) {

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(spellbook.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.weight(1f))
            buttons?.invoke()
        }

        Spacer(Modifier.height(5.dp))
        HorizontalDivider()
        Spacer(Modifier.height(20.dp))

        LazyRow(Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
            itemsIndexed(spellbook.spellcastings, { _, it -> it.name }) { idx, it ->
                Spellcasting(it) {
                    update(
                        spellbook.copy(spellcastings = spellbook.spellcastings.toPersistentList().set(idx, it))
                    )
                }
            }
            item {
                IconButton(onAddSpellcasting) {
                    Icon(Icons.Default.Add, "Add spellcasting")
                }
            }
        }
    }
}