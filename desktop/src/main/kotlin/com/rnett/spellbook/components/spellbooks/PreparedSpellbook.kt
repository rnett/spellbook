package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.components.DragSetState
import com.rnett.spellbook.components.rememberDragSetState
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.KnownSpell
import com.rnett.spellbook.spellbook.LevelKnownSpell
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.withReplace
import com.rnett.spellbook.spellbook.without


@Composable
fun PreparedLevel(
    spells: SpellLevel.Prepared,
    defaultLists: Set<SpellList>,
    level: Int,
    set: (SpellLevel.Prepared) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    val dragSet = rememberDragSetState<Spell>()

    Column(Modifier.fillMaxWidth()) {
        dragSet.display {
            Row {
                Spacer(Modifier.width(15.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, Color.Black),
                    color = Color.DarkGray.copy(alpha = 0.8f).compositeOver(Color.LightGray),
                    elevation = 20.dp
                ) {
                    Row(Modifier.padding(12.dp, 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text(it.name)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(0.1f), verticalAlignment = Alignment.CenterVertically) {
                if (level == 0) {
                    Text("Cantrips")
                } else {
                    Text("Level $level")
                }
            }
        }

        SpellbookDivider()

        spells.prepared.forEachIndexed { idx, it ->
            PreparedSlot(it, dragSet) {
                set(spells.copy(prepared = spells.prepared.withReplace(idx, it)))
            }
        }

        SpellbookDivider()


        spells.known.forEachIndexed { idx, slot ->
            PreparedKnownSpell(
                slot, level,
                { set(spells.copy(known = spells.known.withReplace(idx, it))) },
                { set(spells.copy(known = spells.known.without(idx))) },
                dragSet,
                searchSlot
            )
        }

        SpellbookDivider()

        KnownSpellAdder(spells.known, level, defaultLists) {
            set(spells.copy(known = it))
        }

        Spacer(Modifier.height(4.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreparedSlot(
    spell: Spell?,
    dragSet: DragSetState<Spell>,
    set: (Spell?) -> Unit
) {
    key(spell) {
        var drawerOpen by remember { mutableStateOf(SpellDrawerState.Closed) }

        var draggingOver by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth().ifLet(draggingOver) {
            it.background(Color.White.copy(alpha = 0.2f))
        }) {
            Row(
                Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    .combinedClickable {
                        if (spell != null)
                            drawerOpen = drawerOpen.next
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(10.dp))
                if (spell != null) {
                    Text(spell.name)
                } else {
                    Text("Empty")
                }
            }

            if (spell != null) {
                SpellInfoDrawer(spell, drawerOpen) { drawerOpen = it }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PreparedKnownSpell(
    slot: KnownSpell,
    level: Int,
    set: (KnownSpell) -> Unit,
    remove: () -> Unit,
    dragSet: DragSetState<Spell>,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    KnownSpellSlot(slot, level, set, KnownSpellSlotContext.Prepared(), dragSet, searchSlot)
}
