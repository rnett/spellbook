package com.rnett.spellbook.pages

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import com.rnett.spellbook.components.join
import com.rnett.spellbook.components.spell.SpellListTag
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.LevelSlot
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.SpellSlot
import com.rnett.spellbook.spellbook.SpellbookType
import com.rnett.spellbook.spellbook.Spellcasting
import com.rnett.spellbook.spellbook.withLevel
import com.rnett.spellbook.spellbook.withReplace

@Composable
fun SpellbooksPage(
    spellbooks: List<Pair<String, Spellcasting<*>>>,
    set: (Int, Spellcasting<*>) -> Unit,
    searchSlot: (LevelSlot, (Spell) -> Unit) -> Unit,
) {
    var currentSpellbook: Int? by remember { mutableStateOf(if (spellbooks.isNotEmpty()) 0 else null) }

    Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
        Row {
            Column(Modifier.padding(start = 10.dp, top = 10.dp).weight(0.5f)) {
                if (currentSpellbook != null) {
                    Text(spellbooks[currentSpellbook!!].first, fontWeight = FontWeight.Bold)

                    Divider(Modifier.fillMaxWidth().padding(vertical = 4.dp))

                    SpellbookDisplay(spellbooks[currentSpellbook!!].second, {
                        set(currentSpellbook!!, it)
                    }, searchSlot)
                }
            }
        }
    }
}

@Composable
@Preview
fun EmptySpontaneous() {
    var sorc by remember {
        mutableStateOf(
            Spellcasting.fullCaster(
                SpellbookType.Spontaneous,
                setOf(SpellList.Arcane),
                4
            )
        )
    }
    SpellbookDisplay(sorc, { sorc = it }) { _, _ ->

    }
}

@Composable
fun SpellbookDisplay(
    spellcasting: Spellcasting<*>,
    set: (Spellcasting<*>) -> Unit,
    searchSlot: (LevelSlot, (Spell) -> Unit) -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(spellcasting.type.name)
            Spacer(Modifier.weight(1f))
            Row {
                spellcasting.defaultLists.join({ Spacer(Modifier.width(1.dp)) }) {
                    SpellListTag(it)
                }
            }
            Spacer(Modifier.weight(1f))
            Text(spellcasting.maxLevel.toString())
        }

        (0..spellcasting.maxLevel).forEach { level ->
            if (spellcasting.type == SpellbookType.Spontaneous) {
                SpontaneousLevel(
                    spellcasting[level] as SpellLevel.Spontaneous,
                    level,
                    { set(spellcasting.withLevel(level, it)) },
                    searchSlot
                )
            } else {
                PreparedLevel(
                    spellcasting[level] as SpellLevel.Prepared,
                    level,
                    { set(spellcasting.withLevel(level, it)) },
                    searchSlot
                )
            }
        }

    }
}


@Composable
fun ListsIcon(lists: Set<SpellList>) {
    Text(lists.map { it.name[0] }.joinToString(" "))
}

@Composable
fun SpontaneousLevel(
    spells: SpellLevel.Spontaneous,
    level: Int,
    set: (SpellLevel.Spontaneous) -> Unit,
    searchSlot: (LevelSlot, (Spell) -> Unit) -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            if (level == 0) {
                Text("Cantrips")
            } else {
                Text("Level $level")
            }

            Spacer(Modifier.weight(1f))
            Text("Signature Spells: ${spells.signatures.size} / ${spells.maxSignatures}")
        }

        Divider()

        spells.slots.forEachIndexed { idx, slot ->
            SpontaneousSlot(
                slot,
                level,
                { set(spells.copy(slots = spells.slots.withReplace(idx, it))) },
                idx in spells.signatures,
                searchSlot
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpontaneousSlot(
    slot: SpellSlot,
    level: Int,
    set: (SpellSlot) -> Unit,
    isSignature: Boolean,
    searchSlot: (LevelSlot, (Spell) -> Unit) -> Unit
) {
    Row(Modifier.fillMaxWidth().combinedClickable(onDoubleClick = {
        searchSlot(LevelSlot(level, slot)) {
            set(slot.copy(spell = it))
        }
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
fun PreparedLevel(
    spells: SpellLevel.Prepared,
    level: Int,
    set: (SpellLevel.Prepared) -> Unit,
    searchSlot: (LevelSlot, (Spell) -> Unit) -> Unit
) {

}
