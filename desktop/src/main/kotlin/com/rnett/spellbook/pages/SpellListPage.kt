package com.rnett.spellbook.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.SavedSearchs
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.SidebarDisplay
import com.rnett.spellbook.components.SidebarState
import com.rnett.spellbook.components.filter.FilterDivider
import com.rnett.spellbook.components.filter.SpellFilterEditor
import com.rnett.spellbook.components.search.SpellListFinder
import com.rnett.spellbook.components.search.SpellListSaver
import com.rnett.spellbook.components.spell.SpellDisplay
import com.rnett.spellbook.db.getSpellsForFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.LevelSlot
import com.rnett.spellbook.spellbook.forSlot
import com.rnett.spellbook.spellbook.withoutAnySlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun SpellListPage(
    filter: SpellFilter,
    setFilter: (SpellFilter) -> Unit,
    knownFilters: SavedSearchs,
    updateSavedFilters: (SavedSearchs) -> Unit,
    saveFilter: (String, SpellFilter) -> Unit,
    searchSlot: LevelSlot? = null,
    setSelectedSpell: ((Spell) -> Unit)? = null,
) {
    val savedByFilter = knownFilters.entries.reversed().associate { it.value to it.key }
    val savedNames = knownFilters.keys
    val savedByName = knownFilters.toMap()

    Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
        var loadingSavedFilter by remember { mutableStateOf(false) }

        var spells by remember { mutableStateOf<List<Spell>?>(null) }
        val scrollState = rememberLazyListState()

        LaunchedEffect(filter) {
            withContext(Dispatchers.IO) {
                spells = getSpellsForFilter(filter)
            }
        }

        val sidebarState = remember { SidebarState() }


        Row {
            Column(Modifier.fillMaxWidth(0.15f)) {
                if (setSelectedSpell != null) {
                    Row(Modifier.fillMaxWidth()) {
                        Text("Select spell...")
                        //TODO cancel?
                    }
                }
                if (loadingSavedFilter) {
                    SpellListFinder(knownFilters, updateSavedFilters, { loadingSavedFilter = false }) {
                        setFilter(savedByName.getValue(it).forSlot(searchSlot))
                        loadingSavedFilter = false
                    }
                } else {
                    SpellListSaver(filter, savedByFilter, savedNames, { knownFilters.newName }, { name, filter ->
                        saveFilter(name, filter.ifLet(searchSlot != null) { it.withoutAnySlot() })
                    }) { loadingSavedFilter = true }
                    FilterDivider()
                    SpellFilterEditor(filter, searchSlot) {
                        setFilter(it.forSlot(searchSlot))
                    }
                }
            }

            sidebarState.withNew {
                if (spells != null) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth().weight(0.8f)) {
                        Box(Modifier.padding(top = 10.dp, start = 10.dp, end = 20.dp)) {
                            LazyColumn(state = scrollState) {
                                items(spells!!.toList(), { it.name }) {
                                    Box(Modifier.padding(bottom = 10.dp)) {
                                        SpellDisplay(it, setSelectedSpell)
                                    }
                                }
                            }
                        }
                        val scrollStyle =
                            LocalScrollbarStyle.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
                        VerticalScrollbar(
                            rememberScrollbarAdapter(scrollState),
                            Modifier.align(Alignment.CenterEnd),
                            style = scrollStyle
                        )
                    }
                }
            }

            AnimatedVisibility(sidebarState.active, Modifier.fillMaxWidth().weight(0.2f)) {
                if (sidebarState.current != null) {
                    SidebarDisplay(sidebarState.current!!, sidebarState)
                }
            }

        }
    }
}