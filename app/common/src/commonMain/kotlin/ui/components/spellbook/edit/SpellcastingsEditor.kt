package com.rnett.spellbook.ui.components.spellbook.edit

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.Spellbook
import com.rnett.spellbook.model.spellbook.SpellcastingAndSpells
import com.rnett.spellbook.ui.components.IconButtonWithTooltip
import kotlinx.collections.immutable.toPersistentList


@Composable
fun SpellcastingsEditor(
    spellbook: Spellbook,
    update: (Spellbook) -> Unit,
    startAddSpellcasting: () -> Unit
) {
    Column(Modifier.padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxSize()) {
            IconButtonWithTooltip(Icons.Default.Add, "Add spellcasting", onCLick = startAddSpellcasting)

            Row(Modifier.fillMaxSize().horizontalScroll(rememberScrollState())) {
                spellbook.spellcastings.forEachIndexed { idx, it ->
                    SpellcastingEditor(it, { update ->
                        update(
                            spellbook.copy(
                                spellcastings = spellbook.spellcastings.toPersistentList().set(idx, update)
                            )
                        )
                    }) {
                        update(spellbook.copy(spellcastings = spellbook.spellcastings.toPersistentList().removeAt(idx)))
                    }
                }
            }
        }
    }
}

@Composable
fun SpellcastingEditor(
    spellcasting: SpellcastingAndSpells,
    update: (SpellcastingAndSpells) -> Unit,
    remove: () -> Unit
) {
    val def = spellcasting.spellcastingDef
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(def.name)
        Spacer(Modifier.weight(1f))
        IconButtonWithTooltip(Icons.Default.Delete, "Remove spellcasting", onCLick = remove)
    }
}