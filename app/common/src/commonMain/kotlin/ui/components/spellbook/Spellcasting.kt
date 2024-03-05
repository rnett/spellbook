package com.rnett.spellbook.ui.components.spellbook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.model.spellbook.SpellAtRank
import com.rnett.spellbook.model.spellbook.Spellcasting
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentList

@Composable
fun Spellcasting(spellcasting: Spellcasting, update: (Spellcasting) -> Unit) {
    Column {
        Row {
            Text(spellcasting.name, style = MaterialTheme.typography.headlineMedium)
        }
        HorizontalDivider()

        when (spellcasting) {
            is Spellcasting.Focus -> FocusSpellcasting(spellcasting, update)
            is Spellcasting.Items -> Text("TODO Items")
            is Spellcasting.Prepared -> Text("TODO Prepared")
            is Spellcasting.Spontaneous -> Text("TODO Spontaneous")
            is Spellcasting.Stave -> Text("TODO Stave")
        }

    }
}

@Composable
private fun FocusSpellcasting(spellcasting: Spellcasting.Focus, update: (Spellcasting.Focus) -> Unit) {
    spellcasting.spells.forEachIndexed { idx, it ->
        SpellSlot(SpellAtRank(it, null), persistentSetOf(SpellList.Focus)) {
            if (it == null) {
                update(spellcasting.copy(spells = spellcasting.spells.toPersistentList().removeAt(idx)))
            } else {
                update(spellcasting.copy(spells = spellcasting.spells.toPersistentList().set(idx, it.spell)))
            }
        }
    }
    SpellSlot(null, persistentSetOf(SpellList.Focus)) {
        if (it != null) {
            update(spellcasting.copy(spells = spellcasting.spells.toPersistentList().add(it.spell)))
        }
    }
}