package com.rnett.spellbook.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.preferredHeight
import androidx.compose.foundation.layout.preferredWidthIn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.Spell
import com.rnett.spellbook.asCompose

@Composable
fun SpellHeader(spell: Spell, modifier: Modifier = Modifier) {

    Column(modifier.fillMaxWidth().padding(10.dp)) {

        Row(Modifier.fillMaxHeight(0.5f)) {
            Row(Modifier.fillMaxWidth(0.15f).preferredWidthIn(min = 200.dp).weight(0.3f)) { Text(spell.name, fontWeight = FontWeight.Bold) }


            Row(
                Modifier.fillMaxWidth(0.08f).preferredHeight(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionsTag(spell.actions)
            }

            Row(Modifier.fillMaxWidth(0.2f).weight(0.3f), horizontalArrangement = Arrangement.SpaceEvenly) {
                spell.lists.forEach {
                    SpellListTag(it)
                }
            }

            Row(Modifier.fillMaxWidth(0.15f).weight(0.15f), horizontalArrangement = Arrangement.SpaceEvenly) {
                spell.targeting?.let {
                    it.forEach { targeting ->
                        TargetingTag(targeting)
                    }
                }
            }

            Row(Modifier.fillMaxWidth(0.15f).weight(0.15f), horizontalArrangement = Arrangement.SpaceEvenly) {
                if (spell.requiresAttackRoll)
                    AttackTag()

                spell.save?.let {
                    SaveTag(it, spell.basicSave)
                }
            }

            Row(Modifier.weight(0.4f), horizontalArrangement = Arrangement.SpaceEvenly) {
                spell.conditions.forEach {
                    if (it.isInteresting)
                        ConditionTag(it)
                }
            }

            Box(Modifier.fillMaxWidth(0.15f)) {
                Text(
                    "${spell.type} ${spell.level}",
                    Modifier.align(Alignment.CenterEnd),
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Divider(Modifier.padding(vertical = 10.dp), color = MainColors.spellBodyColor.asCompose())

        Row(Modifier.fillMaxHeight(0.5f)) {
            //TODO consider moving duration to top row, rest to body.  Maybe no color for traits except rarity?
            Row(Modifier.fillMaxWidth(0.1f)) {
                DurationTag(spell.duration, spell.sustained)
            }

            Row(Modifier.fillMaxWidth(0.1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                TraitTag(spell.rarity)
            }

            Row(Modifier.fillMaxWidth(0.1f), horizontalArrangement = Arrangement.SpaceEvenly) {
                spell.school?.let {
                    TraitTag(it)
                }
            }

            Row(Modifier.fillMaxWidth(0.2f).weight(0.5f), horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)) {
                spell.traits.filter { it.isInteresting }.forEach {
                    TraitTag(it)
                }
            }
        }
    }
}