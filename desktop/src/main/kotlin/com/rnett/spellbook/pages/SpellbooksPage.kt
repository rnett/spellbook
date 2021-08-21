package com.rnett.spellbook.pages

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.join
import com.rnett.spellbook.components.spell.SpellListTag
import com.rnett.spellbook.components.spellbooks.PreparedLevel
import com.rnett.spellbook.components.spellbooks.SearchPopup
import com.rnett.spellbook.components.spellbooks.SpontaneousLevel
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.LevelKnownSpell
import com.rnett.spellbook.spellbook.SpellLevel
import com.rnett.spellbook.spellbook.Spellbook
import com.rnett.spellbook.spellbook.Spellcasting
import com.rnett.spellbook.spellbook.SpellcastingType
import com.rnett.spellbook.spellbook.withLevel

@Composable
fun SpellbooksPage(
    spellbooks: List<Pair<String, Spellbook>>,
    set: (Int, Spellbook) -> Unit
) {
    var currentSpellbook: Int? by remember { mutableStateOf(if (spellbooks.isNotEmpty()) 0 else null) }

    var currentSearch by remember { mutableStateOf<Pair<LevelKnownSpell, (Spell) -> Unit>?>(null) }

    val scrollStyle = LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Surface(
        Modifier.fillMaxSize(),
        color = MainColors.outsideColor.asCompose(),
        contentColor = MainColors.textColor.asCompose()
    ) {
        Box(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .padding(start = 10.dp, top = 10.dp)
            ) {
                if (currentSpellbook != null) {
                    val (name, spellbook) = spellbooks[currentSpellbook!!]
                    Text(name, fontSize = 2.em, fontWeight = FontWeight.Bold)

                    Divider(Modifier.fillMaxWidth().padding(vertical = 4.dp))

                    Row(
                        Modifier
                            .fillMaxWidth()
                            .verticalScroll(verticalScrollState)
                            .horizontalScroll(horizontalScrollState)
                    ) {
                        spellbook.spellcastings.forEach { (castingName, casting) ->
                            Column(Modifier.width(1000.dp)) {
                                Text(castingName, fontWeight = FontWeight.Bold, fontSize = 1.5.em)

                                Spacer(Modifier.height(10.dp))

                                SpellcastingDisplay(casting, {
                                    set(currentSpellbook!!, spellbook.withSpellcasting(castingName, it))
                                }) { slot, setter ->
                                    currentSearch = slot to setter
                                }
                            }
                        }
                    }
                }
            }
//
            VerticalScrollbar(
                rememberScrollbarAdapter(verticalScrollState),
                Modifier.align(Alignment.TopEnd),
                style = scrollStyle
            )

            HorizontalScrollbar(
                rememberScrollbarAdapter(horizontalScrollState),
                Modifier.align(Alignment.BottomStart),
                style = scrollStyle
            )
        }
    }

    currentSearch?.let {
        SearchPopup({ currentSearch = null }, it.first, it.second)
    }
}

//TODO(now) make this look nice, do prepared and focus

//TODO allow selecting heightened spells

//TODO prepared should be drag and drop from known into slots

//TODO something like a shopping cart.  Add from search page, drag out into spellbooks and groups

//TODO groups

//TODO search by name

//TODO options to open spells and sidebar pages in browser


@Composable
fun SpellcastingDisplay(
    spellcasting: Spellcasting<*>,
    set: (Spellcasting<*>) -> Unit,
    searchSlot: (LevelKnownSpell, (Spell) -> Unit) -> Unit
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(spellcasting.type.name, fontSize = 1.5.em)
        Spacer(Modifier.weight(1f))
        Row {
            spellcasting.defaultLists.join({ Spacer(Modifier.width(1.dp)) }) {
                SpellListTag(it)
            }
        }
        Spacer(Modifier.weight(1f))
        Text(spellcasting.maxLevel.toString())
    }

    (0..spellcasting.maxLevel).forEach { level ->
        if (spellcasting.type == SpellcastingType.Spontaneous) {
            SpontaneousLevel(
                spellcasting[level] as SpellLevel.Spontaneous,
                spellcasting.defaultLists,
                level,
                {
                    set(spellcasting.withLevel(level, it))
                },
                searchSlot
            )
        } else {
            PreparedLevel(
                spellcasting[level] as SpellLevel.Prepared,
                spellcasting.defaultLists,
                level,
                { set(spellcasting.withLevel(level, it)) },
                searchSlot
            )
        }
    }
}


