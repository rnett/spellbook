package com.rnett.spellbook.components.filter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.spell.ActionTypeTag
import com.rnett.spellbook.components.spell.ActionsTag
import com.rnett.spellbook.components.spell.AttackTag
import com.rnett.spellbook.components.spell.ConditionTag
import com.rnett.spellbook.components.spell.DurationTag
import com.rnett.spellbook.components.spell.SaveTag
import com.rnett.spellbook.components.spell.SpellListTag
import com.rnett.spellbook.components.spell.TargetingTag
import com.rnett.spellbook.components.spell.TraitTag
import com.rnett.spellbook.components.spell.TypeTag
import com.rnett.spellbook.data.allDurations
import com.rnett.spellbook.data.allTargeting
import com.rnett.spellbook.data.interestingSpellConditions
import com.rnett.spellbook.data.nonSpecialSpellTraits
import com.rnett.spellbook.data.spellConditionsByName
import com.rnett.spellbook.data.traitsByName
import com.rnett.spellbook.filter.ActionFilter
import com.rnett.spellbook.filter.AttackTypeFilter
import com.rnett.spellbook.filter.DurationFilter
import com.rnett.spellbook.filter.LevelFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.filter.filter
import com.rnett.spellbook.spell.CastActionType
import com.rnett.spellbook.spell.Rarity
import com.rnett.spellbook.spell.Save
import com.rnett.spellbook.spell.School
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spell.SpellType
import com.rnett.spellbook.spellbook.LevelKnownSpell
import kotlin.math.max
import kotlin.math.min


private val pickableAttackTypes =
    (Save.values().map { AttackTypeFilter.TargetSave(it, null) } + AttackTypeFilter.Attack).toSet()

private val pickableActionTypes =
    setOf(
        ActionFilter.Free,
        ActionFilter.Single,
        ActionFilter.Double,
        ActionFilter.Triple,
        ActionFilter.Reaction,
        ActionFilter.Duration
    )

class ExpansionManager {
    val components: MutableList<Boolean> = mutableStateListOf<Boolean>()

    fun closeAll() {
        components.indices.forEach {
            components[it] = false
        }
    }

    @Composable
    fun component(): Component = remember {
        components.add(false)
        Component(components.size - 1)
    }

    inner class Component(val idx: Int) {
        inline fun expand(expanded: Boolean) {
            closeAll()
            components[idx] = expanded
        }

        inline val expanded get() = components[idx]
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpellFilterEditor(
    filter: SpellFilter,
    presetSlot: LevelKnownSpell? = null,
    showReset: Boolean = true,
    update: (SpellFilter) -> Unit
) {
    Box {
        val scrollState = remember { ScrollState(0) }
        Column(Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp, end = 20.dp).verticalScroll(scrollState)) {
            val expandedStates = remember { ExpansionManager() }

            if (presetSlot == null) {
                FilterEditor(filter.lists,
                    SpellList.lists,
                    { update(filter.copy(lists = it)) },
                    expandedStates.component(),
                    { Text("Spell Lists") }) {
                    SpellListTag(it)
                }

                OptionalBoolean(filter.isFocus, { update(filter.copy(isFocus = it)) }) {
                    Text("Focus")
                }

                FilterEditor(filter.types,
                    SpellType.values().toSet(),
                    { update(filter.copy(types = it)) },
                    expandedStates.component(),
                    { Text("Spell Type") }) {
                    TypeTag(it)
                }
            }

            val levelLimit = presetSlot?.level ?: 10

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Min Level")
                Spacer(Modifier.weight(1f))
                IconButtonHand({
                    update(filter.copy(level = filter.level.copy(min = filter.level.min - 1)))
                }, enabled = filter.level.min > 1) {
                    IconWithTooltip(Icons.Default.Remove, "Minus")
                }
                Text(filter.level.min.toString())
                IconButtonHand({
                    val newValue = filter.level.min + 1
                    update(filter.copy(level = LevelFilter(newValue, max(filter.level.max, newValue))))
                }, enabled = filter.level.min < levelLimit) {
                    IconWithTooltip(Icons.Default.Add, "Plus")
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Max Level")
                Spacer(Modifier.weight(1f))
                IconButtonHand({
                    val newValue = filter.level.max - 1
                    update(filter.copy(level = LevelFilter(min(newValue, filter.level.min), newValue)))
                }, enabled = filter.level.max > 1) {
                    IconWithTooltip(Icons.Default.Remove, "Minus")
                }
                Text(filter.level.max.toString())
                IconButtonHand({
                    update(filter.copy(level = filter.level.copy(max = filter.level.max + 1)))
                }, enabled = filter.level.max < levelLimit) {
                    IconWithTooltip(Icons.Default.Add, "Plus")
                }
            }

            FilterDivider(true)

            FilterEditor(filter.attackTypes,
                pickableAttackTypes,
                { update(filter.copy(attackTypes = it)) },
                expandedStates.component(),
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
                expandedStates.component(),
                { Text("Conditions") }) {
                ConditionTag(spellConditionsByName.getValue(it.name), sidebar = false)
            }

            OptionalBoolean(filter.incapacitation, { update(filter.copy(incapacitation = it)) }) {
                Text("Incapacitation")
            }

            FilterEditor(filter.actions,
                pickableActionTypes,
                { update(filter.copy(actions = it)) },
                expandedStates.component(),
                { Text("Actions") }) {
                Box(Modifier.height(24.dp)) {
                    ActionsTag(it.toActions())
                }
            }

            OptionalBoolean(filter.hasManipulate, { update(filter.copy(hasManipulate = it)) }) {
                Text("Manipulate")
            }

            FilterEditor(filter.targeting,
                allTargeting,
                { update(filter.copy(targeting = it)) },
                expandedStates.component(),
                { Text("Targeting") }) {
                TargetingTag(it)
            }

            FilterEditor(filter.duration,
                allDurations.map { DurationFilter(it) }.toSet(),
                { update(filter.copy(duration = it)) },
                expandedStates.component(),
                { Text("Duration") }) {
                DurationTag(it.duration, false)
            }

            OptionalBoolean(filter.sustained, { update(filter.copy(sustained = it)) }) {
                Text("Sustained")
            }

            SetEditor(filter.hasActionTypes.orEmpty(),
                CastActionType.values().toSet(),
                { update(filter.copy(hasActionTypes = it.ifEmpty { null })) },
                expandedStates.component(),
                { Text("Required Cast Actions") }) {
                ActionTypeTag(it)
            }

            SetEditor(filter.doesntHaveActionTypes.orEmpty(),
                CastActionType.values().toSet(),
                { update(filter.copy(doesntHaveActionTypes = it.ifEmpty { null })) },
                expandedStates.component(),
                { Text("Forbidden Cast Actions") }) {
                ActionTypeTag(it)
            }

            FilterEditor(filter.rarity, Rarity.traits, { update(filter.copy(rarity = it)) }, expandedStates.component(),
                { Text("Rarity") }) {
                Box(Modifier.height(24.dp)) {
                    TraitTag(traitsByName.getValue(it.name), sidebar = false)
                }
            }

            FilterEditor(filter.schools,
                School.schools,
                { update(filter.copy(schools = it)) },
                expandedStates.component(),
                { Text("School") }) {
                TraitTag(traitsByName.getValue(it.name), sidebar = false)
            }

            OptionalBoolean(filter.hasSummons, { update(filter.copy(hasSummons = it)) }) {
                Text("Has Summons")
            }

            OptionalBoolean(filter.hasHeightening, { update(filter.copy(hasHeightening = it)) }) {
                Text("Heightenable")
            }

            FilterEditor(filter.traits,
                nonSpecialSpellTraits.map { it.key }.toSet(),
                { update(filter.copy(traits = it)) },
                expandedStates.component(),
                { Text("Traits") }) {
                TraitTag(traitsByName.getValue(it.name), sidebar = false)
            }

            if (showReset) {
                Spacer(Modifier.height(10.dp))

                Button(
                    {
                        expandedStates.closeAll()
                        update(SpellFilter())
                    },
                    Modifier.fillMaxWidth().align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(backgroundColor = FilterColors.cancelReset.asCompose())
                ) {
                    Text("RESET", textAlign = TextAlign.Center)
                }
            }

        }
        val scrollStyle = LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
        VerticalScrollbar(
            rememberScrollbarAdapter(scrollState),
            Modifier.align(Alignment.CenterEnd),
            style = scrollStyle
        )
    }
}