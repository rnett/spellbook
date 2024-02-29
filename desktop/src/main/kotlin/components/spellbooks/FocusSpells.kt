package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.Add
import androidx.compose.material3.icons.filled.OfflineBolt
import androidx.compose.material3.icons.outlined.DeleteForever
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.LocalMainState
import com.rnett.spellbook.components.*
import com.rnett.spellbook.components.spell.ShortSpellDisplay
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.pages.SpellSearch
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
    searchSlot: SpellSearch,
    showInfo: (Spell) -> Unit,
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
            { set(focusSpellcasting.copy(cantrips = it)) },
            { searchSlot(spellSlotFor(true), it) },
            showInfo
        )



        FocusSpellList(
            "Spells",
            focusSpellcasting.spells,
            spellSlotFor(false),
            { set(focusSpellcasting.copy(spells = it)) },
            { searchSlot(spellSlotFor(false), it) },
            showInfo
        )


    }
}

@Composable
private fun FocusSpellList(
    name: String,
    list: Set<Spell>,
    slotSpec: SpellSlotSpec,
    set: (Set<Spell>) -> Unit,
    search: ((Spell) -> Unit) -> Unit,
    showInfo: (Spell) -> Unit,
) {
    val dragSet = LocalMainState.current.dragSpellsFromSide
    var isDraggingOver by remember { mutableStateOf(false) }

    //TODO lines between spells, open bottom info bar on click
    //TODO line under name

    Column(Modifier.fillMaxWidth()
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
        }, horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            Modifier.fillMaxWidth(),
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

        SpellbookStyleDivider(Modifier.fillMaxWidth())
        Column(Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
            list.join({ }) {
                Row(
                    Modifier.fillMaxWidth().clickable { showInfo(it) }.padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShortSpellDisplay(it, Modifier.fillMaxWidth(0.9f), showLists = false, showLevel = true)
                    IconButtonHand(
                        { set(list - it) },
                        Modifier.size(24.dp)
                    ) {
                        IconWithTooltip(
                            Icons.Outlined.DeleteForever,
                            "Remove",
                            tint = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        if (list.isNotEmpty())
            SpellbookStyleDivider(Modifier.fillMaxWidth())
    }
}