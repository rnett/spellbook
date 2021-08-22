package com.rnett.spellbook.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.zIndex
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.spellbooks.SearchPopup
import com.rnett.spellbook.components.spellbooks.SpellInfoDrawer
import com.rnett.spellbook.components.spellbooks.SpellcastingHeader
import com.rnett.spellbook.components.spellbooks.SpellcastingLevel
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.LevelKnownSpell
import com.rnett.spellbook.spellbook.Spellbook

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellbooksPage(
    spellbooks: List<Pair<String, Spellbook>>,
    set: (Int, Spellbook) -> Unit,
) {
    val castingsPerPage = 3
    var currentSpellbook: Int? by remember { mutableStateOf(if (spellbooks.isNotEmpty()) 0 else null) }

    var currentSearch by remember { mutableStateOf<Pair<LevelKnownSpell, (Spell) -> Unit>?>(null) }

    val scrollStyle = LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    var infoDrawerSpell by remember { mutableStateOf<Spell?>(null) }

    Surface(
        Modifier.fillMaxSize(),
        color = MainColors.outsideColor.asCompose(),
        contentColor = MainColors.textColor.asCompose()
    ) {
        Box(Modifier.fillMaxSize()) {
            val density = LocalDensity.current
            var width by remember { mutableStateOf(0.dp) }
            Column(
                Modifier
                    .padding(top = 10.dp)
                    .onGloballyPositioned { with(density) { width = it.size.width.toDp() } }
            ) {

                if (currentSpellbook != null) {
                    Column(Modifier.padding(top = 10.dp).weight(1f)) {
                        val (name, spellbook) = spellbooks[currentSpellbook!!]
                        Text(name, fontSize = 2.em, fontWeight = FontWeight.Bold)

                        Divider(Modifier.fillMaxWidth().padding(vertical = 4.dp))

                        Row(Modifier.fillMaxWidth().horizontalScroll(horizontalScrollState)) {
                            spellbook.spellcastings.forEach {
                                Box(Modifier.width(width / castingsPerPage)) {
                                    SpellcastingHeader(it.key, it.value)
                                }
                            }
                        }

                        Column(
                            Modifier
                                .fillMaxWidth()
                                .verticalScroll(verticalScrollState)
                        ) {
                            val maxLevel = spellbook.spellcastings.maxOf { it.value.maxLevel }
                            (0..maxLevel).forEach { level ->
                                Row(Modifier.fillMaxWidth().horizontalScroll(horizontalScrollState)) {
                                    spellbook.spellcastings.forEach { (castingName, casting) ->
                                        Column(Modifier.width(width / castingsPerPage)) {
                                            SpellcastingLevel(
                                                casting,
                                                level,
                                                {
                                                    set(currentSpellbook!!, spellbook.withSpellcasting(castingName, it))
                                                },
                                                {
                                                    infoDrawerSpell = it
                                                }
                                            ) { slot, setter ->
                                                currentSearch = slot to setter
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                AnimatedVisibility(
                    infoDrawerSpell != null,
                    Modifier.fillMaxWidth()
                ) {
                    Column {
                        //TODO button to open in search (needs name search first)
                        //TODO close button?
                        infoDrawerSpell?.let {
                            SpellInfoDrawer(it) { infoDrawerSpell = null }
                        }
                        Spacer(Modifier.height(scrollStyle.thickness))
                    }
                }
            }

            VerticalScrollbar(
                rememberScrollbarAdapter(verticalScrollState),
                Modifier.align(Alignment.TopEnd),
                style = scrollStyle
            )



            HorizontalScrollbar(
                rememberScrollbarAdapter(horizontalScrollState),
                Modifier.align(Alignment.BottomStart).zIndex(3f),
                style = scrollStyle
            )
        }
    }

    currentSearch?.let {
        SearchPopup({ currentSearch = null }, it.first, it.second)
    }
}

//TODO(now) make this look nice, do prepared and focus

//TODO move spell detail panel to bottom panel?

//TODO something like a shopping cart.  Add from search page, drag out into spellbooks and groups

//TODO groups

//TODO search by name

//TODO options to open spells and sidebar pages in browser

