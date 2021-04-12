package com.rnett.spellbook.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarStyleAmbient
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.CastActionType
import com.rnett.spellbook.Rarity
import com.rnett.spellbook.Save
import com.rnett.spellbook.School
import com.rnett.spellbook.SpellList
import com.rnett.spellbook.SpellType
import com.rnett.spellbook.data.interestingSpellConditions
import com.rnett.spellbook.data.nonSpecialSpellTraits
import com.rnett.spellbook.data.spellConditionsByName
import com.rnett.spellbook.data.spellTraitsByName
import com.rnett.spellbook.filter.ActionFilter
import com.rnett.spellbook.filter.AttackTypeFilter
import com.rnett.spellbook.filter.LevelFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.filter.filter
import kotlin.math.max
import kotlin.math.min


private val pickableAttackTypes = (Save.values().map { AttackTypeFilter.TargetSave(it, null) } + AttackTypeFilter.Attack).toSet()

private val pickableActionTypes =
    setOf(ActionFilter.Free, ActionFilter.Single, ActionFilter.Double, ActionFilter.Triple, ActionFilter.Reaction, ActionFilter.Duration)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpellFilterEditor(filter: SpellFilter, presetSlot: Boolean = false, update: (SpellFilter) -> Unit) {
    Box {
        val scrollState = rememberScrollState()
        Column(Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 20.dp).verticalScroll(scrollState)) {
            val expandedStates = remember { mutableListOf<MutableState<Boolean>>() }

            if (!presetSlot) {
                FilterEditor(filter.lists, SpellList.lists, { update(filter.copy(lists = it)) }, expandedStates,
                    { Text("Spell Lists") }) {
                    SpellListTag(it)
                }

                OptionalBoolean(filter.isFocus, { update(filter.copy(isFocus = it)) }) {
                    Text("Focus")
                }

                FilterEditor(filter.types, SpellType.values().toSet(), { update(filter.copy(types = it)) }, expandedStates,
                    { Text("Spell Type") }) {
                    TypeTag(it)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Min Level")
                    Spacer(Modifier.weight(1f))
                    IconButton({
                        update(filter.copy(level = filter.level.copy(min = filter.level.min - 1)))
                    }, enabled = filter.level.min > 1) {
                        Icon(Icons.Default.Remove, "Minus")
                    }
                    Text(filter.level.min.toString())
                    IconButton({
                        val newValue = filter.level.min + 1
                        update(filter.copy(level = LevelFilter(newValue, max(filter.level.max, newValue))))
                    }, enabled = filter.level.min < 10) {
                        Icon(Icons.Default.Add, "Plus")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Max Level")
                    Spacer(Modifier.weight(1f))
                    IconButton({
                        val newValue = filter.level.max - 1
                        update(filter.copy(level = LevelFilter(min(newValue, filter.level.min), newValue)))
                    }, enabled = filter.level.max > 1) {
                        Icon(Icons.Default.Remove, "Minus")
                    }
                    Text(filter.level.max.toString())
                    IconButton({
                        update(filter.copy(level = filter.level.copy(max = filter.level.max + 1)))
                    }, enabled = filter.level.max < 10) {
                        Icon(Icons.Default.Add, "Plus")
                    }
                }

                FilterDivider(true)
            }

            FilterEditor(filter.attackTypes, pickableAttackTypes, { update(filter.copy(attackTypes = it)) }, expandedStates,
                { Text("Attack Type") }) {
                when (it) {
                    AttackTypeFilter.Attack -> AttackTag()
                    is AttackTypeFilter.TargetSave -> SaveTag(it.save, false)
                }
            }

            OptionalBoolean(filter.persistentDamage, { update(filter.copy(persistentDamage = it)) }) {
                Text("Persistent Damage")
            }

            FilterEditor(filter.conditions,
                interestingSpellConditions.map { it.filter }.toSet(),
                { update(filter.copy(conditions = it)) },
                expandedStates,
                { Text("Conditions") }) {
                Box(Modifier.height(24.dp)) {
                    ConditionTag(spellConditionsByName.getValue(it.name), sidebar = false)
                }
            }

            FilterEditor(filter.actions, pickableActionTypes, { update(filter.copy(actions = it)) }, expandedStates,
                { Text("Actions") }) {
                Box(Modifier.height(24.dp)) {
                    ActionsTag(it.toActions())
                }
            }

            OptionalBoolean(filter.hasManipulate, { update(filter.copy(hasManipulate = it)) }) {
                Text("Manipulate")
            }

            OptionalBoolean(filter.sustained, { update(filter.copy(sustained = it)) }) {
                Text("Sustained")
            }

            SetEditor(filter.hasActionTypes.orEmpty(),
                CastActionType.values().toSet(),
                { update(filter.copy(hasActionTypes = it.ifEmpty { null })) },
                expandedStates,
                { Text("Required Cast Actions") }) {
                ActionTypeTag(it)
            }

            SetEditor(filter.doesntHaveActionTypes.orEmpty(),
                CastActionType.values().toSet(),
                { update(filter.copy(doesntHaveActionTypes = it.ifEmpty { null })) },
                expandedStates,
                { Text("Forbidden Cast Actions") }) {
                ActionTypeTag(it)
            }

            FilterEditor(filter.rarity, Rarity.traits, { update(filter.copy(rarity = it)) }, expandedStates,
                { Text("Rarity") }) {
                Box(Modifier.height(24.dp)) {
                    TraitTag(spellTraitsByName.getValue(it.name), sidebar = false)
                }
            }

            FilterEditor(filter.schools, School.schools, { update(filter.copy(schools = it)) }, expandedStates,
                { Text("School") }) {
                Box(Modifier.height(24.dp)) {
                    TraitTag(spellTraitsByName.getValue(it.name), sidebar = false)
                }
            }

            OptionalBoolean(filter.hasSummons, { update(filter.copy(hasSummons = it)) }) {
                Text("Has Summons")
            }

            OptionalBoolean(filter.hasHeightening, { update(filter.copy(hasHeightening = it)) }) {
                Text("Heightenable")
            }

            FilterEditor(filter.traits, nonSpecialSpellTraits.map { it.key }.toSet(), { update(filter.copy(traits = it)) }, expandedStates,
                { Text("Traits") }) {
                TraitTag(spellTraitsByName.getValue(it.name), sidebar = false)
            }

            Spacer(Modifier.height(10.dp))

            Button(
                {
                    expandedStates.forEach { it.value = false }
                    update(SpellFilter())
                },
                Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red.copy(alpha = 0.4f))
            ) {
                Text("RESET", textAlign = TextAlign.Center)
            }

        }
        val scrollStyle = ScrollbarStyleAmbient.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
        VerticalScrollbar(rememberScrollbarAdapter(scrollState),
            Modifier.align(Alignment.CenterEnd),
            scrollStyle)
    }
}