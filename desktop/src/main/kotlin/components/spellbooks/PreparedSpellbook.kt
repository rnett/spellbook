package com.rnett.spellbook.components.spellbooks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OfflineBolt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.OfflineBolt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.components.DragSetState
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconMaxSetter
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.rememberDragSetState
import com.rnett.spellbook.components.spell.ShortSpellDisplay
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
    setLevel: (SpellLevel.Prepared) -> Unit,
    openInfoDrawer: (Spell) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {

    @Suppress("NAME_SHADOWING") val setLevel = rememberUpdatedState(setLevel)

    fun set(level: SpellLevel.Prepared) {
        setLevel.value(level.copy(prepared = level.prepared.filter { level.known.any { l -> l.spell == it } }))
    }

    val dragSet = rememberDragSetState<Spell>()

    SpellcastingLevelDisplay(dragSet, {
        Row(Modifier.weight(0.1f), verticalAlignment = Alignment.CenterVertically) {
            if (level == 0) {
                Text("Cantrips")
            } else {
                Text("Level $level")
            }
        }

        Row(Modifier.weight(0.3f), verticalAlignment = Alignment.CenterVertically) {
            IconMaxSetter(
                spells.prepared.size,
                spells.maxPrepared,
                {
                    set(spells.copy(maxPrepared = it))
                },
                {
                    Icon(Icons.Filled.OfflineBolt, "Filled spell slot")
                }) {
                Icon(Icons.Outlined.OfflineBolt, "Empty spell slot")
            }
        }

    }) {

        spells.prepared.forEachIndexed { idx, it ->
            PreparedSlot(
                it,
                dragSet,
                {
                    set(spells.copy(prepared = spells.prepared.without(idx)))
                },
                openInfoDrawer
            )
        }

        SpellbookDivider(noStartPadding = true)

        spells.known.forEachIndexed { idx, slot ->
            PreparedKnownSpell(
                slot, level,
                { set(spells.copy(known = spells.known.withReplace(idx, it))) },
                { set(spells.copy(known = spells.known.without(idx))) },
                {
                    set(spells.copy(prepared = spells.prepared.toMutableList().apply {
                        add(it)
                        sort()
                    }))
                },
                spells.prepared.size < spells.maxPrepared,
                dragSet,
                openInfoDrawer,
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
    spell: Spell,
    dragSet: DragSetState<Spell>,
    remove: () -> Unit,
    openInfoDrawer: (Spell) -> Unit
) {

    key(spell) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .clickable {
                    openInfoDrawer(spell)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(10.dp))

            IconButtonHand({ remove() }, Modifier.size(24.dp)) {
                IconWithTooltip(
                    Icons.Outlined.Close,
                    "Remove",
                    tint = Color.Red.copy(alpha = 0.7f)
                )
            }

            Spacer(Modifier.width(20.dp))

            ShortSpellDisplay(spell, showLists = false)

            Spacer(Modifier.weight(0.6f).widthIn(min = 40.dp))
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
    prepare: (Spell) -> Unit,
    canPrepare: Boolean,
    dragSet: DragSetState<Spell>,
    openInfoDrawer: (Spell) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    KnownSpellSlot(
        slot,
        level,
        set,
        KnownSpellSlotContext.Prepared(prepare, remove, canPrepare),
        dragSet,
        openInfoDrawer,
        searchSlot
    )
}
