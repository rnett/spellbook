package com.rnett.spellbook.components.spell

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList

//TODO flag for incapacitate

@Composable
fun SpellHeader(spell: Spell, modifier: Modifier = Modifier, setSelectedSpell: ((Spell) -> Unit)?) {

    Column(modifier.fillMaxWidth().padding(10.dp)) {

        val rowMod = Modifier

        Row(rowMod, verticalAlignment = Alignment.CenterVertically) {
            var textModifier = Modifier.fillMaxWidth(0.15f).widthIn(min = 200.dp).weight(0.3f)
            if (setSelectedSpell != null) {
                IconButtonHand({ setSelectedSpell(spell) }, Modifier.size(24.dp)) {
                    IconWithTooltip(Icons.Default.CheckCircleOutline, "Select Spell")
                }
                Spacer(Modifier.width(10.dp))
                textModifier = textModifier.padding(bottom = 2.dp)
            }
            Row(textModifier) {
                Text(
                    spell.name,
                    fontWeight = FontWeight.Bold,
                )
            }


            Row(
                Modifier.fillMaxWidth(0.08f).height(20.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionsTag(spell.actions)
            }

            Row(Modifier.fillMaxWidth(0.2f).weight(0.3f), horizontalArrangement = Arrangement.SpaceEvenly) {
                SpellList.lists.minus(SpellList.Other).forEach {
                    SpellListTag(it, it !in spell.lists)
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

        Row(rowMod, verticalAlignment = Alignment.CenterVertically) {
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

            Row(
                Modifier.fillMaxWidth(0.2f).weight(0.5f),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.Start)
            ) {
                spell.traits.filter { it.isInteresting }.forEach {
                    TraitTag(it)
                }
            }
        }
    }
}