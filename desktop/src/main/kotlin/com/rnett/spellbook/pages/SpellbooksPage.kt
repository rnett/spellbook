package com.rnett.spellbook.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.LevelSlot
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.SpellSlot
import com.rnett.spellbook.spellbook.Spellbook
import com.rnett.spellbook.spellbook.SpellbookType

@Composable
fun SpellbooksPage(spellbooks: List<Pair<String, Spellbook<*>>>, searchSlot: (LevelSlot) -> Unit) {
    var currentSpellbook: Int? by remember { mutableStateOf(if (spellbooks.isNotEmpty()) 0 else null) }

    Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
        Row {
            Column(Modifier.padding(start = 10.dp, top = 10.dp).weight(0.5f)) {
                if (currentSpellbook != null) {
                    Text(spellbooks[currentSpellbook!!].first, fontWeight = FontWeight.Bold)

                    Divider(Modifier.fillMaxWidth().padding(vertical = 4.dp))

                    SpellbookDisplay(spellbooks[currentSpellbook!!].second, searchSlot)
                }
            }
        }
    }
}

@Composable
fun SpellbookDisplay(spellbook: Spellbook<*>, searchSlot: (LevelSlot) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(spellbook.type.name)
            Spacer(Modifier.weight(1f))
            Text(spellbook.maxLevel.toString())
        }

        (0..spellbook.maxLevel).forEach {
            if (spellbook.type == SpellbookType.Spontaneous) {
                SpontaneousLevel(spellbook[it] as SpellLevel.Spontaneous, it, searchSlot)
            } else {
                PreparedLevel(spellbook[it] as SpellLevel.Prepared, it, searchSlot)
            }
        }

    }
}


@Composable
fun ListsIcon(lists: Set<SpellList>) {
    Text(lists.map { it.name[0] }.joinToString(" "))
}

@Composable
fun SpontaneousLevel(spells: SpellLevel.Spontaneous, level: Int, searchSlot: (LevelSlot) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            if (level == 0) {
                Text("Cantrips")
            } else {
                Text("Level $level")
            }

            Spacer(Modifier.weight(1f))
            Text("Signature Spells: ${spells.signatures.size} / ${spells.numSignatures}")
        }

        Divider()

        spells.slots.forEachIndexed { idx, slot ->
            SpontaneousSlot(slot, level, idx in spells.signatures, searchSlot)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpontaneousSlot(slot: SpellSlot, level: Int, isSignature: Boolean, searchSlot: (LevelSlot) -> Unit) {
    Row(Modifier.fillMaxWidth().combinedClickable(onDoubleClick = {
        searchSlot(LevelSlot(level, slot))
    }) { }) {
        ListsIcon(slot.lists)
        Spacer(Modifier.width(10.dp))

        if (isSignature) {
            Text("!")
            Spacer(Modifier.width(2.dp))
        }

        Text(slot.spell?.name ?: "Empty")
    }
}

@Composable
fun PreparedLevel(spells: SpellLevel.Prepared, level: Int, searchSlot: (LevelSlot) -> Unit) {

}
