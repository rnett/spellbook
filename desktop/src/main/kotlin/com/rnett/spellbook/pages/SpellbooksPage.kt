package com.rnett.spellbook.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.window.Popup
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.SavedSearchColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.CenterPopup
import com.rnett.spellbook.components.DragSetState
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconMaxSetter
import com.rnett.spellbook.components.IconSetter
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.core.FlowRow
import com.rnett.spellbook.components.draggableContainer
import com.rnett.spellbook.components.draggableItem
import com.rnett.spellbook.components.focusableEditable
import com.rnett.spellbook.components.handPointer
import com.rnett.spellbook.components.join
import com.rnett.spellbook.components.onEnter
import com.rnett.spellbook.components.onEscape
import com.rnett.spellbook.components.rememberDragSetState
import com.rnett.spellbook.components.spell.ActionsTag
import com.rnett.spellbook.components.spell.SpellDisplay
import com.rnett.spellbook.components.spell.SpellListShortTag
import com.rnett.spellbook.components.spell.SpellListTag
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.KnownSpell
import com.rnett.spellbook.spellbook.LevelKnownSpell
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.SpellbookType
import com.rnett.spellbook.spellbook.Spellcasting
import com.rnett.spellbook.spellbook.withLevel
import com.rnett.spellbook.spellbook.withReplace

@Composable
fun SpellbooksPage(
    spellbooks: List<Pair<String, Spellcasting<*>>>,
    set: (Int, Spellcasting<*>) -> Unit
) {
    var currentSpellbook: Int? by remember { mutableStateOf(if (spellbooks.isNotEmpty()) 0 else null) }

    var currentSearch by remember { mutableStateOf<Pair<LevelKnownSpell, (Spell) -> Unit>?>(null) }

    Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
        Row {
            Column(Modifier.padding(start = 10.dp, top = 10.dp).weight(0.5f)) {
                if (currentSpellbook != null) {
                    Text(spellbooks[currentSpellbook!!].first, fontWeight = FontWeight.Bold)

                    Divider(Modifier.fillMaxWidth().padding(vertical = 4.dp))

                    SpellbookDisplay(spellbooks[currentSpellbook!!].second, {
                        set(currentSpellbook!!, it)
                    }) { slot, setter ->
                        currentSearch = slot to setter
                    }
                }
            }
        }
    }

    currentSearch?.let {
        SearchPopup({ currentSearch = null }, it.first, it.second)
    }
}

//TODO(now) make this look nice

//TODO prepared should be drag and drop from known into slots

//TODO should be able to drag around spontaneous ones to other slots, too, as long as the spell lists match

//TODO button to remove spell.  On clicks, open if spell is there, search if not.  No searching when spell is there

//TODO something like a shopping cart.  Add from search page, drag out into spellbooks and groups

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
                        SpellFilter(),
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

@Composable
fun SpellbookDisplay(
    spellcasting: Spellcasting<*>,
    set: (Spellcasting<*>) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    Row {
        val scrollState = rememberScrollState()

        Column(Modifier.weight(1f).verticalScroll(scrollState)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(spellcasting.type.name, fontSize = 1.5.em)
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
                        {
                            set(spellcasting.withLevel(level, it))
                        },
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

        val scrollStyle = LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
        VerticalScrollbar(
            rememberScrollbarAdapter(scrollState),
            Modifier,
            style = scrollStyle
        )
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

@Composable
fun SpontaneousLevel(
    spells: SpellLevel.Spontaneous,
    level: Int,
    set: (SpellLevel.Spontaneous) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit,
) {

    val dragSet = rememberDragSetState<Spell>()

    Column(Modifier.fillMaxWidth()) {
        dragSet.display {
            Row {
                Spacer(Modifier.width(10.dp))
                Text(it.name)
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

            if (level != 0) {

                Row(Modifier.weight(0.3f), verticalAlignment = Alignment.CenterVertically) {
                    IconSetter(spells.numSlots, { set(spells.copy(numSlots = it)) }) {
                        IconWithTooltip(Icons.Default.LocalFireDepartment, "Spell Slot")
                    }
                }

                Row(Modifier.weight(0.3f), verticalAlignment = Alignment.CenterVertically) {
                    IconMaxSetter(
                        spells.signatures.size,
                        spells.maxSignatures,
                        { set(spells.copy(maxSignatures = it)) },
                        {
                            Icon(Icons.Filled.Star, "Filled Signature")
                        }) {
                        Icon(Icons.Filled.StarOutline, "Open Signature")
                    }
                }
            }
        }

        Spacer(Modifier.height(2.dp))

        spells.known.forEachIndexed { idx, slot ->
            SpontaneousSlot(
                idx,
                slot,
                level,
                {
                    set(spells.copy(known = spells.known.withReplace(idx, it)))
                },
                idx in spells.signatures,
                { set(spells.copy(signatures = if (it) spells.signatures + idx else spells.signatures - idx)) },
                spells.signatures.size < spells.maxSignatures,
                dragSet,
                searchSlot
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpontaneousSlot(
    idx: Int,
    slot: KnownSpell,
    level: Int,
    set: (KnownSpell) -> Unit,
    isSignature: Boolean,
    setSignature: (Boolean) -> Unit,
    canBeSignature: Boolean,
    dragSet: DragSetState<Spell>,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    var drawerOpen by remember { mutableStateOf(SpellDrawerState.Closed) }

    Column(Modifier.fillMaxWidth()) {

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
                it.draggableItem(dragSet, slot.spell!!) {
                    println("Drug from $idx")
                    set(slot.copy(spell = null))
                }
            }.ifLet(slot.spell == null) {
                it.draggableContainer(dragSet,
                    onEnter = {
                        println("Entered $idx with ${it.name}")
                    },
                    onLeave = {
                        println("Left $idx with ${it.name}")
                    }) {
                    println("Drug ${it.name} to $idx")
                    set(slot.copy(spell = it))
                }
            }



        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.width(15.dp), verticalAlignment = Alignment.CenterVertically) {
                if (level > 0) {
                    if (isSignature) {
                        IconButtonHand({ setSignature(false) }, Modifier.size(15.dp)) {
                            IconWithTooltip(Icons.Filled.Star, "Signature")
                        }
                    } else {
                        IconButtonHand(
                            { setSignature(true) },
                            Modifier.size(15.dp),
                            enabled = canBeSignature && slot.spell != null
                        ) {
                            IconWithTooltip(Icons.Outlined.StarOutline, "Make Signature")
                        }
                    }
                }
            }

            Spacer(Modifier.width(10.dp))

            Row(Modifier.width(120.dp), verticalAlignment = Alignment.CenterVertically) {
                ListsIcon(slot.lists) { set(slot.copy(lists = it)) }
            }

            Spacer(Modifier.width(10.dp))

            slot.spell?.let { spell ->
                Text(spell.name)
                Spacer(Modifier.width(10.dp))

                Box(Modifier.height(20.dp)) {
                    ActionsTag(spell.actions)
                }

                Spacer(Modifier.width(10.dp))

                FlowRow(Modifier.weight(0.2f).widthIn(max = 200.dp), horizontalGap = 4.dp) {
                    spell.lists.forEach {
                        SpellListTag(it)
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

        if (slot.spell != null) {
            SpellInfoDrawer(slot.spell!!, drawerOpen) { drawerOpen = it }
        }
    }
}

enum class SpellDrawerState {
    Header, Full, Closed;

    val next
        get() = when (this) {
            Header -> Full
            Full -> Closed
            Closed -> Header
        }

    val changeExpanded
        get() = when (this) {
            Header -> Full
            Full -> Header
            else -> Closed
        }
}

@Composable
fun SpellInfoDrawer(spell: Spell, state: SpellDrawerState, setState: (SpellDrawerState) -> Unit) {
    if (state == SpellDrawerState.Closed) return
    val focusRequester = remember { FocusRequester() }

    key(state) {
        SideEffect {
            if (state == SpellDrawerState.Closed)
                focusRequester.freeFocus()
            else
                focusRequester.requestFocus()
        }
    }

    Box(Modifier
        .focusRequester(focusRequester)
        .focusable()
        .onEscape { setState(SpellDrawerState.Closed) }
        .onEnter { setState(state.next) }
    ) {
        SpellDisplay(spell, null, state == SpellDrawerState.Full) {
            setState(state.changeExpanded)
        }
    }
}

@Composable
fun PreparedLevel(
    spells: SpellLevel.Prepared,
    level: Int,
    set: (SpellLevel.Prepared) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {

}
