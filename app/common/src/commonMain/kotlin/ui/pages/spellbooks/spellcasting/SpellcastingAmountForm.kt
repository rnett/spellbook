package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.model.spellbook.LevelMap
import com.rnett.spellbook.model.spellbook.SpellcastingAmount
import com.rnett.spellbook.model.spellbook.SpellcastingArchetypeFeat
import com.rnett.spellbook.ui.components.IntField
import com.rnett.spellbook.ui.components.LabeledCheckbox
import kotlinx.collections.immutable.toImmutableMap

@Composable
fun SpellcastingAmountForm(archetype: Boolean, isBounded: Boolean, setAmount: (SpellcastingAmount) -> Unit) {
    if (archetype) {
        ArchetypeAmountForm(isBounded, setAmount)
    } else {
        FullAmountForm(isBounded, setAmount)
    }
}

@Composable
private fun FullAmountForm(isBounded: Boolean, setAmount: (SpellcastingAmount) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        var slotsPerRank by remember { mutableStateOf<Int?>(if (isBounded) 2 else 3) }
        var tenthSlot by remember { mutableStateOf(!isBounded) }
        var secondTenthSlot by remember { mutableStateOf(false) }

        IntField(
            slotsPerRank,
            { slotsPerRank = it },
            minimum = 0,
            label = { Text("Slots per rank") },
        )

        if (!isBounded) {
            Spacer(Modifier.width(20.dp))

            LabeledCheckbox(tenthSlot, { tenthSlot = it }) {
                Text("Rank 10 slot class feature")
            }

            Spacer(Modifier.width(20.dp))

            LabeledCheckbox(secondTenthSlot, { secondTenthSlot = it }) {
                Text("Rank 10 slot feat")
            }
        }

        LaunchedEffect(slotsPerRank, tenthSlot, secondTenthSlot) {
            if (slotsPerRank != null) {
                if (isBounded) {
                    setAmount(SpellcastingAmount.Bounded(slotsPerRank!!))
                } else {
                    setAmount(SpellcastingAmount.Full(slotsPerRank!!, tenthSlot, secondTenthSlot))
                }
            }
        }
    }
}

@Composable
private fun ArchetypeAmountForm(isBounded: Boolean, setAmount: (SpellcastingAmount) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        var dedicationTakenAt by remember { mutableStateOf<Int?>(null) }
        var basicTakenAt by remember { mutableStateOf<Int?>(null) }
        var expertTakenAt by remember { mutableStateOf<Int?>(null) }
        var masterTakenAt by remember { mutableStateOf<Int?>(null) }
        var breadthTakenAt by remember { mutableStateOf<Int?>(null) }

        SideEffect {
            if (dedicationTakenAt == null) {
                basicTakenAt = null
                expertTakenAt = null
                masterTakenAt = null
                breadthTakenAt = null
            }
            if (basicTakenAt == null) {
                expertTakenAt = null
                masterTakenAt = null
            }
            if (expertTakenAt == null) {
                masterTakenAt = null
            }
        }

        LaunchedEffect(dedicationTakenAt, basicTakenAt, expertTakenAt, masterTakenAt, breadthTakenAt) {
            val takenAtMap = mutableMapOf<SpellcastingArchetypeFeat, Int>()
            if (dedicationTakenAt != null) {
                takenAtMap[SpellcastingArchetypeFeat.Dedication] = dedicationTakenAt!!
                if (basicTakenAt != null) {
                    takenAtMap[SpellcastingArchetypeFeat.Basic] = basicTakenAt!!
                    if (expertTakenAt != null) {
                        takenAtMap[SpellcastingArchetypeFeat.Expert] = expertTakenAt!!
                        if (masterTakenAt != null) {
                            takenAtMap[SpellcastingArchetypeFeat.Master] = masterTakenAt!!
                        }
                    }
                }
                if (isBounded)
                    setAmount(SpellcastingAmount.BoundedArchetype(takenAtMap.toImmutableMap()))
                else
                    setAmount(SpellcastingAmount.FullArchetype(breadthTakenAt, takenAtMap.toImmutableMap()))
            }
        }

        Column {
            IntField(
                dedicationTakenAt,
                { dedicationTakenAt = it },
                minimum = LevelMap.MIN_LEVEL,
                maximum = LevelMap.MAX_LEVEL,
                label = { Text("Dedication feat taken at") },
            )

            IntField(
                basicTakenAt,
                { basicTakenAt = it },
                minimum = dedicationTakenAt,
                maximum = LevelMap.MAX_LEVEL,
                label = { Text("Basic spellcasting feat taken at") },
                enabled = dedicationTakenAt != null,
            )

            IntField(
                expertTakenAt,
                { expertTakenAt = it },
                minimum = basicTakenAt,
                maximum = LevelMap.MAX_LEVEL,
                label = { Text("Expert spellcasting feat taken at") },
                enabled = basicTakenAt != null,
            )

            IntField(
                masterTakenAt,
                { masterTakenAt = it },
                minimum = expertTakenAt,
                maximum = LevelMap.MAX_LEVEL,
                label = { Text("Master spellcasting feat taken at") },
                enabled = expertTakenAt != null,
            )
        }

        if (!isBounded) {
            IntField(
                breadthTakenAt,
                { breadthTakenAt = it },
                minimum = dedicationTakenAt,
                maximum = LevelMap.MAX_LEVEL,
                label = { Text("Breadth feat taken at") },
                enabled = dedicationTakenAt != null,
            )
        }
    }
}