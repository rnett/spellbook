package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spell.SpellList
import com.rnett.spellbook.model.spellbook.LeveledExtraSpells
import com.rnett.spellbook.model.spellbook.Spellcasting
import com.rnett.spellbook.model.spellbook.SpellcastingAmount
import com.rnett.spellbook.model.spellbook.SpellcastingSpec
import com.rnett.spellbook.ui.components.LabeledCheckbox
import kotlinx.collections.immutable.persistentMapOf

data class PreparedSpellcastingBuilder(val isFlexible: Boolean) : SpellcastingBuilder {
    @Composable
    override fun Render(addButton: @Composable (Spellcasting?) -> Unit) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            var name by remember { mutableStateOf("") }
            var archetype by remember { mutableStateOf(false) }
            var bounded by remember { mutableStateOf(false) }
            var amount by remember { mutableStateOf<SpellcastingAmount?>(null) }
            var spellList by remember { mutableStateOf<SpellList?>(null) }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                SpellcastingName(name) { name = it }
                SpellcastingSpellList(spellList) { spellList = it }
                ArchetypeSelector(archetype) { archetype = it }
                LabeledCheckbox(bounded, { bounded = it }) {
                    Text("Bounded spellcasting")
                }
            }

            SpellcastingAmountForm(archetype, bounded) {
                amount = it
            }

            if (name.isNotBlank() && amount != null && spellList != null) {
                val spec = if (isFlexible) {
                    SpellcastingSpec.Flexible(amount!!, spellList!!, LeveledExtraSpells(persistentMapOf()))
                } else {
                    SpellcastingSpec.Prepared(amount!!, spellList!!, LeveledExtraSpells(persistentMapOf()))
                }
                addButton(spec.toSpellcasting(name))
            } else {
                addButton(null)
            }
        }
    }
}