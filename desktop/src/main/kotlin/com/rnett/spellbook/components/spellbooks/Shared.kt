package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.CenterPopup
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.focusableEditable
import com.rnett.spellbook.components.handPointer
import com.rnett.spellbook.components.onEscape
import com.rnett.spellbook.components.spell.SpellListShortTag
import com.rnett.spellbook.filter.LevelFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.pages.SpellListPage
import com.rnett.spellbook.pages.SpellListState
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import com.rnett.spellbook.spellbook.KnownSpell
import com.rnett.spellbook.spellbook.LevelKnownSpell


@Composable
fun KnownSpellAdder(
    known: List<KnownSpell>,
    level: Int,
    defaultLists: Set<SpellList>,
    set: (List<KnownSpell>) -> Unit
) {
    Row {
        Spacer(Modifier.width(50.dp))
        IconButtonHand(
            {
                set(
                    known.dropLast(1)
                )
            },
            Modifier.size(24.dp),
            enabled = known.isNotEmpty() && known.last().spell == null
        ) {
            IconWithTooltip(Icons.Default.Remove, "Remove Known Spell")
        }
        Spacer(Modifier.width(50.dp))
        IconButtonHand(
            {
                set(
                    known + KnownSpell(
                        defaultLists,
                        if (level == 0) SpellType.Cantrip else SpellType.Spell
                    )
                )
            },
            Modifier.size(24.dp)
        ) {
            IconWithTooltip(Icons.Default.Add, "Add Known Spell")
        }
    }
}

@Composable
fun SearchPopup(
    close: () -> Unit,
    level: LevelKnownSpell,
    setSpell: (Spell) -> Unit
) {
    Popup(
        CenterPopup,
        onDismissRequest = { close() },
        onPreviewKeyEvent = onEscape(close),
        focusable = true
    ) {
        Surface(
            Modifier.fillMaxSize(0.9f),
            color = MainColors.outsideColor.withAlpha(0.6f).asCompose().compositeOver(Color.Black),
            border = BorderStroke(2.dp, Color.Black),
            elevation = 5.dp
        ) {
            Column(Modifier.fillMaxSize()) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(close) {
                        IconWithTooltip(Icons.Default.Close, "Close")
                    }
                }
                val state = remember {
                    SpellListState.FindForSpellbook(
                        SpellFilter(level = LevelFilter(level.level)),
                        level
                    ) {
                        setSpell(it)
                        close()
                    }
                }
                SpellListPage(state)
            }
        }
    }
}


@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun ListsIcon(lists: Set<SpellList>, set: (Set<SpellList>) -> Unit) {
    var editing by remember { mutableStateOf(false) }

    Row(
        Modifier
            .focusableEditable(editing) { editing = it }
            .combinedClickable(onDoubleClick = {
                editing = !editing
            }) {}
            .ifLet(!editing) { it.handPointer() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!editing) {
            lists.sorted().forEach {
                Box(Modifier.padding(2.dp)) {
                    SpellListShortTag(it)
                }
            }
        } else {
            SpellList.traditions.forEach {
                Box(Modifier
                    .padding(2.dp)
                    .handPointer()
                    .clickable(
                        it !in lists || lists.size > 1
                    ) {
                        if (it in lists)
                            set(lists - it)
                        else
                            set(lists + it)
                    }) {
                    SpellListShortTag(it, if (it in lists) 1.0f else 0.1f)
                }
            }
            Spacer(Modifier.width(8.dp))
            IconButtonHand({ editing = false }, Modifier.size(20.dp).handPointer()) {
                IconWithTooltip(Icons.Default.Close, "Done")
            }
        }

    }
}