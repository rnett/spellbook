package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.LocalMainState
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconSetter
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.draggableContainer
import com.rnett.spellbook.components.spell.ShortSpellDisplay
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import com.rnett.spellbook.spellbook.FocusSpellcasting
import com.rnett.spellbook.spellbook.KnownSpell
import com.rnett.spellbook.spellbook.SpellSlotSpec

@Composable
fun FocusSpells(
    focusSpellcasting: FocusSpellcasting,
    set: (FocusSpellcasting) -> Unit,
    searchSlot: (SpellSlotSpec, (Spell) -> Unit) -> Unit
) {
    fun spellSlotFor(isCantrip: Boolean) =
        SpellSlotSpec(10, KnownSpell(setOf(SpellList.Focus), SpellType.Focus, isCantrip), false)
    Column(Modifier.fillMaxWidth().padding(20.dp, 10.dp)) {
        Row(Modifier.fillMaxWidth()) {
            Text("Focus Spells", Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 1.5.em)
            IconSetter(focusSpellcasting.maxPoints, { set(focusSpellcasting.copy(maxPoints = it)) }) {
                IconWithTooltip(Icons.Filled.OfflineBolt, "Focus Point")
            }
            Spacer(Modifier.weight(0.2f))
        }

        SpellbookStyleDivider(Modifier.fillMaxWidth())

        FocusSpellList(
            "Cantrips",
            focusSpellcasting.cantrips,
            spellSlotFor(true),
            { set(focusSpellcasting.copy(cantrips = it)) }) { set ->
            searchSlot(spellSlotFor(true), set)
        }

        SpellbookStyleDivider(Modifier.fillMaxWidth())

        FocusSpellList(
            "Spells",
            focusSpellcasting.spells,
            spellSlotFor(false),
            { set(focusSpellcasting.copy(spells = it)) }) { set ->
            searchSlot(spellSlotFor(false), set)
        }


    }
}

@Composable
private fun FocusSpellList(
    name: String,
    list: Set<Spell>,
    slotSpec: SpellSlotSpec,
    set: (Set<Spell>) -> Unit,
    search: ((Spell) -> Unit) -> Unit
) {
    val dragSet = LocalMainState.current.dragSpellsFromSide
    var isDraggingOver by remember { mutableStateOf(false) }

    Row(Modifier.fillMaxWidth()
        .draggableContainer(dragSet,
            onEnter = { isDraggingOver = true },
            onLeave = { isDraggingOver = false },
            accepts = { slotSpec.accepts(it) },
            onDrop = {
                set(list + it)
                true
            }
        )
        .ifLet(isDraggingOver) {
            it.background(Color.White.copy(alpha = 0.3f))
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 1.2.em)
        IconButtonHand(
            {
                search {
                    set(list + it)
                }
            },
            Modifier.size(28.dp)
        ) {
            IconWithTooltip(Icons.Default.Add, "Add")
        }
        Spacer(Modifier.weight(0.2f))
    }

    list.forEach {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            ShortSpellDisplay(it, Modifier.fillMaxWidth(0.9f))
            IconButtonHand({
                set(list - it)
            }, Modifier.size(24.dp)) {
                IconWithTooltip(
                    Icons.Outlined.DeleteForever,
                    "Remove",
                    tint = Color.Red.copy(alpha = 0.7f)
                )
            }
        }
    }
}