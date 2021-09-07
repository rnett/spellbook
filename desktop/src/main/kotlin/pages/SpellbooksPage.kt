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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import com.rnett.spellbook.MutableNamedList
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.spellbooks.FocusSpells
import com.rnett.spellbook.components.spellbooks.SearchPopup
import com.rnett.spellbook.components.spellbooks.SpellInfoDrawer
import com.rnett.spellbook.components.spellbooks.SpellbookStyleDivider
import com.rnett.spellbook.components.spellbooks.SpellcastingHeader
import com.rnett.spellbook.components.spellbooks.SpellcastingLevel
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.SpellSlotSpec
import com.rnett.spellbook.spellbook.Spellbook
import java.lang.Integer.min

const val MAX_CASTINGS_PER_PAGE = 3

typealias SpellSearch = (SpellSlotSpec, (Spell) -> Unit) -> Unit

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SpellbooksPage(
    spellbooks: MutableNamedList<Spellbook>,
) {
    var currentSpellbook: String? by remember { mutableStateOf(if (spellbooks.isNotEmpty()) spellbooks.first().first else null) }

    var currentSearch by remember { mutableStateOf<Pair<SpellSlotSpec, (Spell) -> Unit>?>(null) }

    Surface(
        Modifier.fillMaxSize(),
        color = MainColors.outsideColor.asCompose(),
        contentColor = MainColors.textColor.asCompose()
    ) {
        currentSpellbook?.let { name ->
            val spellbook by derivedStateOf { spellbooks[name]!! }
            SpellbookEditor(name, spellbook, {
                spellbooks[name] = it
            }) { slot, set ->
                currentSearch = slot to set
            }
        }
    }

    currentSearch?.let {
        SearchPopup({ currentSearch = null }, it.first, it.second)
    }
}

@OptIn(ExperimentalAnimationApi::class, androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun SpellbookEditor(name: String, spellbook: Spellbook, set: (Spellbook) -> Unit, search: SpellSearch) {

    var infoDrawerSpell by remember { mutableStateOf<Spell?>(null) }

    val scrollStyle = LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }

    val verticalScrollState = rememberLazyListState()
    val horizontalScrollState = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        val density = LocalDensity.current
        var width by remember { mutableStateOf(0.dp) }
        Column(
            Modifier
                .padding(top = 10.dp)
                .onGloballyPositioned { with(density) { width = it.size.width.toDp() } }
        ) {

            SpellbookHeader(name, spellbook, set)
            val castingsPerPage = min(spellbook.spellcastings.size, MAX_CASTINGS_PER_PAGE)

            val pageWidth by derivedStateOf { width / castingsPerPage }
            val maxLevel = spellbook.spellcastings.maxOf { it.second.maxLevel }

            LazyColumn(Modifier.padding(top = 10.dp).weight(1f), verticalScrollState) {

                item {
                    FocusSpells(
                        spellbook.focus,
                        { set(spellbook.copy(focus = it)) },
                        search,
                        { infoDrawerSpell = it }
                    )

                    SpellbookStyleDivider(Modifier.fillMaxWidth().padding(vertical = 4.dp))
                }

                stickyHeader(spellbook.spellcastings) {
                    Row(Modifier.fillMaxWidth().horizontalScroll(horizontalScrollState)) {
                        spellbook.spellcastings.forEach { (name, spellcasting) ->
                            Box(Modifier.width(pageWidth)) {
                                SpellcastingHeader(name, spellcasting)
                            }
                        }
                    }
                }

                items(maxLevel) { level ->
                    Row(Modifier.fillMaxWidth().horizontalScroll(horizontalScrollState)) {
                        spellbook.spellcastings.forEach { (castingName, casting) ->
                            Column(Modifier.width(pageWidth)) {
                                SpellcastingLevel(
                                    casting,
                                    level,
                                    {
                                        set(spellbook.withSpellcasting(castingName, it))
                                    },
                                    {
                                        infoDrawerSpell = it
                                    },
                                    search
                                )
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

@Composable
private fun SpellbookHeader(name: String, spellbook: Spellbook, set: (Spellbook) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        Text(name, fontSize = 2.em, fontWeight = FontWeight.Bold)
        Spacer(Modifier.weight(0.9f))

        IconButton({}, Modifier.size(28.dp)) {
            IconWithTooltip(Icons.Default.Add, "Add spellcasting")
        }

        Spacer(Modifier.weight(0.1f))
    }
}

//TODO(now) make this look nice, do focus

//TODO something like a shopping cart.  Add from search page, drag out into spellbooks and groups

//TODO groups

//TODO options to open spells and sidebar pages in browser

