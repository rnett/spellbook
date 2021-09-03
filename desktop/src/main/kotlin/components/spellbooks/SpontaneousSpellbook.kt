package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.components.DragSetState
import com.rnett.spellbook.components.IconMaxSetter
import com.rnett.spellbook.components.IconSetter
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.rememberDragSetState
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.KnownSpell
import com.rnett.spellbook.spellbook.LevelKnownSpell
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.withReplace


@Composable
fun SpontaneousLevel(
    spells: SpellLevel.Spontaneous,
    defaultLists: Set<SpellList>,
    level: Int,
    set: (SpellLevel.Spontaneous) -> Unit,
    openInfoDrawer: (Spell) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit,
) {
    @Suppress("NAME_SHADOWING") val set by rememberUpdatedState(set)
    val dragSet = rememberDragSetState<Spell>()

    SpellcastingLevelDisplay(dragSet, {
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
                    IconWithTooltip(Icons.Filled.OfflineBolt, "Spell Slot")
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
    }) {

        spells.known.forEachIndexed { idx, slot ->
            SpontaneousSlot(
                slot,
                level,
                {
                    set(spells.copy(known = spells.known.withReplace(idx, it)))
                },
                idx in spells.signatures,
                { set(spells.copy(signatures = if (it) spells.signatures + idx else spells.signatures - idx)) },
                spells.signatures.size < spells.maxSignatures,
                dragSet,
                openInfoDrawer,
                searchSlot
            )
        }

        SpellbookDivider()

        KnownSpellAdder(spells.known, level, defaultLists) {
            spells.copy(known = it)
        }

        Spacer(Modifier.height(4.dp))
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
    dragSet: DragSetState<Spell>,
    openInfoDrawer: (Spell) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    KnownSpellSlot(
        slot,
        level,
        set,
        KnownSpellSlotContext.Spontaneous(isSignature, setSignature, canBeSignature),
        dragSet,
        openInfoDrawer,
        searchSlot
    )
}