package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.model.spellbook.LeveledExtraSpells
import com.rnett.spellbook.model.spellbook.RankMap
import com.rnett.spellbook.ui.components.DenseRankMap
import com.rnett.spellbook.ui.components.DropdownSelector
import com.rnett.spellbook.ui.components.PlaceholderTransformation
import com.rnett.spellbook.ui.components.RequiredText
import com.rnett.spellbook.ui.components.SmallIntField
import com.rnett.spellbook.ui.components.SparseLevelMapForm

@Composable
fun SpellcastingName(name: String, setName: (String) -> Unit) {
    TextField(
        name, setName,
        label = {
            RequiredText("Name")
        },
        visualTransformation = PlaceholderTransformation,
        singleLine = true,
    )
}

@Composable
fun ArchetypeSelector(archetype: Boolean, setArchetype: (Boolean) -> Unit) {
    SingleChoiceSegmentedButtonRow {
        SegmentedButton(!archetype, { setArchetype(false) }, SegmentedButtonDefaults.itemShape(0, 2), icon = {}) {
            Text("Full class")
        }
        SegmentedButton(archetype, { setArchetype(true) }, SegmentedButtonDefaults.itemShape(1, 2), icon = {}) {
            Text("Archetype")
        }
    }
}


@Composable
fun SpellcastingSpellList(spellList: SpellList?, set: (SpellList) -> Unit) {
    DropdownSelector(
        SpellList.nonFocusLists,
        spellList,
        { it.name },
        label = { RequiredText("Spell list") },
        visualTransformation = PlaceholderTransformation,
        onSelected = set
    )
}

@Composable
fun SpellcastingExtraSlotsForm(extraSlots: LeveledExtraSpells, update: (LeveledExtraSpells) -> Unit) {
    Column {
        Text("Extra spell slots", style = MaterialTheme.typography.titleLarge)
        Text(
            "Later levels override previous ones. \"All levels\" is added to each, as is the \"*\" rank.",
            style = MaterialTheme.typography.bodySmall
        )

        Column(Modifier.padding(20.dp)) {
            SparseLevelMapForm(extraSlots, update, ::RankMap) { level, extraSpells, update ->
                val realExtras = extraSpells ?: RankMap()

                Column(Modifier.padding(start = 10.dp)) {
                    DenseRankMap(realExtras, { update(it) }) { rank, extras, update ->
                        SmallIntField(extras, update, minimum = 0)
                    }
                }
            }
        }
    }
}