package com.rnett.spellbook.components.spell

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.LocalMainState
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.draggableItem
import com.rnett.spellbook.spell.Spell
import kotlinx.coroutines.flow.Flow


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellDisplay(spell: Spell, setSelectedSpell: ((Spell) -> Unit)?, expanded: Boolean, headerClick: () -> Unit) {
    val dragSet = LocalMainState.current.dragSpellsToSide
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MainColors.spellBorderColor.asCompose(),
        modifier = Modifier.draggableItem(dragSet, spell)
    ) {

        Column(Modifier) {
            SpellHeader(
                spell,
                Modifier.clickable(remember { MutableInteractionSource() }, null) { headerClick() },
                setSelectedSpell
            )

            Surface(Modifier.fillMaxWidth(), color = MainColors.spellBodyColor.asCompose()) {

                AnimatedVisibility(expanded) {

                    if (expanded) {
                        SpellBody(spell)
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellDisplay(spell: Spell, setSelectedSpell: ((Spell) -> Unit)?, globalExpanded: Flow<Boolean>) {
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(globalExpanded) {
        globalExpanded.collect {
            expanded = it
        }
    }

    SpellDisplay(spell, setSelectedSpell, expanded) { expanded = !expanded }
}