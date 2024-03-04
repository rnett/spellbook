package com.rnett.spellbook.model.spellbook

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.data.SpellbooksDao
import com.rnett.spellbook.model.spellbook.dao.SpellbooksDao

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SpellbookPreview(
    spellbook: Spellbook,
    modifier: Modifier = Modifier,
    buttons: @Composable() (() -> Unit)? = null,
    dao: SpellbooksDao? = null
) {
    ElevatedCard(modifier, elevation = CardDefaults.elevatedCardElevation(5.dp)) {
        Column(Modifier.padding(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(spellbook.name)
                Spacer(Modifier.weight(1f))
                if (dao != null) {
                    SpellbooksDao(dao)

                    if (buttons != null) {
                        Spacer(Modifier.width(10.dp))
                        buttons.invoke()
                    }
                }
            }

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                spellbook.spellcastings.forEach {
                    Text(it.name)
                }
            }
        }
    }
}