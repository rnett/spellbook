package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.SavedSearchColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.DragSetState
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.core.FlowRow
import com.rnett.spellbook.components.draggableContainer
import com.rnett.spellbook.components.draggableItem
import com.rnett.spellbook.components.spell.ActionsTag
import com.rnett.spellbook.components.spell.SpellListTag
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.KnownSpell
import com.rnett.spellbook.spellbook.LevelKnownSpell

sealed class KnownSpellSlotContext {
    data class Spontaneous(val isSignature: Boolean, val setSignature: (Boolean) -> Unit, val canBeSignature: Boolean) :
        KnownSpellSlotContext()

    data class Prepared(val i: Int = 0) : KnownSpellSlotContext()
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KnownSpellSlot(
    slot: KnownSpell,
    level: Int,
    set: (KnownSpell) -> Unit,
    context: KnownSpellSlotContext,
    dragSet: DragSetState<Spell>,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    key(slot.spell) {
        var drawerOpen by remember { mutableStateOf(SpellDrawerState.Closed) }

        var draggingOver by remember { mutableStateOf(false) }
        var beingDragged by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth().ifLet(draggingOver) {
            it.background(Color.White.copy(alpha = 0.2f))
        }) {

            val modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                .combinedClickable(onDoubleClick = {
                    if (slot.spell == null)
                        searchSlot(LevelKnownSpell(level, slot)) {
                            set(slot.copy(spell = it))
                        }
                    else
                        drawerOpen = drawerOpen.next
                }) {
                    if (slot.spell != null)
                        drawerOpen = drawerOpen.next
                }.ifLet(slot.spell != null) {
                    it.draggableItem(
                        dragSet, slot.spell!!,
                        onDragStart = {
                            beingDragged = true
                        },
                        onDragCancel = {
                            beingDragged = false
                        }
                    ) {
                        beingDragged = false
                        set(slot.copy(spell = null))
                    }
                }.ifLet(slot.spell == null && context is KnownSpellSlotContext.Spontaneous) {
                    it.draggableContainer(dragSet,
                        accepts = {
                            it.lists.any { it in slot.lists }
                        },
                        onEnter = {
                            draggingOver = true
                        },
                        onLeave = {
                            draggingOver = false
                        }
                    ) {
                        set(slot.copy(spell = it))
                    }
                }

            Row(modifier, verticalAlignment = Alignment.CenterVertically) {
                Row(Modifier.width(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    if (context is KnownSpellSlotContext.Spontaneous) {
                        if (level > 0) {
                            if (context.isSignature) {
                                IconButtonHand({ context.setSignature(false) }, Modifier.size(15.dp)) {
                                    IconWithTooltip(Icons.Filled.Star, "Signature")
                                }
                            } else {
                                IconButtonHand(
                                    { context.setSignature(true) },
                                    Modifier.size(15.dp),
                                    enabled = context.canBeSignature && slot.spell != null
                                ) {
                                    IconWithTooltip(Icons.Outlined.StarOutline, "Make Signature")
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.width(10.dp))

                Row(Modifier.width(120.dp), verticalAlignment = Alignment.CenterVertically) {
                    ListsIcon(slot.lists) { set(slot.copy(lists = it)) }
                }

                Spacer(Modifier.width(10.dp))

                if (beingDragged) {
                    Divider(Modifier.fillMaxWidth(0.6f).height(1.dp).background(Color.White))
                } else {
                    slot.spell?.let { spell ->
                        Row(Modifier.weight(0.15f), verticalAlignment = Alignment.CenterVertically) {
                            Text(spell.name)
                        }

                        Row(
                            Modifier.weight(0.05f).height(20.dp).widthIn(min = 24.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ActionsTag(spell.actions)
                        }

                        FlowRow(Modifier.weight(0.3f).widthIn(min = 50.dp, max = 200.dp), horizontalGap = 10.dp) {
                            spell.lists.forEach {
                                //TODO make small tags
                                SpellListTag(it)
                            }
                        }
                        Row(Modifier.weight(1.5f)) {
                            IconButtonHand({ set(slot.copy(spell = null)) }, Modifier.size(24.dp)) {
                                IconWithTooltip(
                                    Icons.Outlined.DeleteForever,
                                    "Remove",
                                    tint = Color.Red.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    if (slot.spell == null) {
                        Text("Empty")
                        Spacer(Modifier.width(20.dp))
                        IconButtonHand(
                            {
                                searchSlot(LevelKnownSpell(level, slot)) {
                                    set(slot.copy(spell = it))
                                }
                            },
                            Modifier.size(24.dp)
                                .background(SavedSearchColors.searchButtonColor.asCompose(), RoundedCornerShape(5.dp))
                        ) {
                            IconWithTooltip(Icons.Default.Search, "Find spell")
                        }
                    }
                }
            }

            if (slot.spell != null) {
                SpellInfoDrawer(slot.spell!!, drawerOpen) { drawerOpen = it }
            }
        }
    }
}