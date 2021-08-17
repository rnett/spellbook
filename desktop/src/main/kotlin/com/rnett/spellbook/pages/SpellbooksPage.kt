package com.rnett.spellbook.pages

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconMaxSetter
import com.rnett.spellbook.components.IconSetter
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.handPointer
import com.rnett.spellbook.components.join
import com.rnett.spellbook.components.spell.SpellListShortTag
import com.rnett.spellbook.components.spell.SpellListTag
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
    set: (Int, Spellcasting<*>) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit,
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

//TODO(now) make this look nice

//TODO prepared should be drag and drop from known into slots

//TODO I want some kind of icon number setter, i.e. - * * * +, w/ an option for max.  For slots & signatures

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
            .combinedClickable(onDoubleClick = {
                editing = !editing
            }) {}
            .ifLet(!editing) { it.handPointer() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!editing) {
            lists.forEach {
                Box(Modifier.padding(2.dp)) {
                    SpellListShortTag(it)
                }
            }
        } else {
            SpellList.traditions.forEach {
                Box(Modifier
                    .padding(horizontal = 2.dp)
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
    Column(Modifier.fillMaxWidth()) {
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
                slot,
                level,
                { set(spells.copy(known = spells.known.withReplace(idx, it))) },
                idx in spells.signatures,
                { set(spells.copy(signatures = if (it) spells.signatures + idx else spells.signatures - idx)) },
                spells.signatures.size < spells.maxSignatures,
                searchSlot
            )
        }

        Spacer(Modifier.height(12.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpontaneousSlot(
    slot: KnownSpell,
    level: Int,
    set: (KnownSpell) -> Unit,
    isSignature: Boolean,
    setSignature: (Boolean) -> Unit,
    canBeSignature: Boolean,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp).combinedClickable(onDoubleClick = {
        searchSlot(LevelKnownSpell(level, slot)) {
            set(slot.copy(spell = it))
        }
    }) { }, verticalAlignment = Alignment.CenterVertically) {

        if (level > 0) {
            if (isSignature) {
                IconButtonHand({ setSignature(false) }, Modifier.size(15.dp)) {
                    IconWithTooltip(Icons.Filled.Star, "Signature")
                }
            } else {
                IconButtonHand({ setSignature(true) }, Modifier.size(15.dp), enabled = canBeSignature) {
                    IconWithTooltip(Icons.Outlined.StarOutline, "Make Signature")
                }
            }
        }

        Spacer(Modifier.width(10.dp))

        ListsIcon(slot.lists) { set(slot.copy(lists = it)) }

        Spacer(Modifier.width(10.dp))

        Text(slot.spell?.name ?: "Empty")
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
