package com.rnett.spellbook.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarStyleAmbient
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.TurnedIn
import androidx.compose.material.icons.filled.TurnedInNot
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.referentialEqualityPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.FilterColors
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.Spell
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.db.getSpellsForFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.newName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun SpellListSaver(filter: SpellFilter, savedFilters: Map<SpellFilter, String>, savedNames: Set<String>, saveFilter: (String, SpellFilter) -> Unit) {
    Row(Modifier.padding(vertical = 5.dp).height(30.dp), verticalAlignment = Alignment.CenterVertically) {
        if (filter in savedFilters) {
            Spacer(Modifier.width(12.dp))
            Icon(Icons.Default.TurnedIn, "Saved")
            Spacer(Modifier.width(4.dp))
            Text(savedFilters.getValue(filter))
        } else {
            var filterName: String? by remember { mutableStateOf(null) }

            if (filterName == null) {
                IconButton({
                    filterName = savedNames.newName()
                }, enabled = filter != SpellFilter()) {
                    Icon(Icons.Default.TurnedInNot, "Save")
                }
            } else {
                IconButton({
                    filterName = null
                }) {
                    Icon(Icons.Outlined.Cancel, "Cancel")
                }

                SmallTextField(filterName!!, { filterName = it },
                    Modifier.weight(0.5f),
                    isError = filterName!! in savedNames,
                    singleLine = true,
                    colors = TextFieldDefaults.textFieldColors(
                        cursorColor = FilterColors.dividerColor.asCompose(),
                        errorCursorColor = FilterColors.dividerColor.asCompose(),
                        focusedIndicatorColor = FilterColors.dividerColor.asCompose()
                    )
                )

                IconButton({
                    saveFilter(filterName!!, filter)
                }, enabled = filterName!! !in savedNames) {
                    Icon(Icons.Default.BookmarkAdd, "Save")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun SpellListPage(
    initialFilter: SpellFilter = SpellFilter(),
    savedFilters: Map<SpellFilter, String>,
    savedNames: Set<String>,
    saveFilter: (String, SpellFilter) -> Unit,
) {
    Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
        var filter by remember { mutableStateOf(initialFilter, referentialEqualityPolicy()) }

        var spells by remember { mutableStateOf<Set<Spell>?>(null) }
        val scrollState = rememberLazyListState()

        LaunchedEffect(filter) {
            withContext(Dispatchers.IO) {
                spells = getSpellsForFilter(filter)
            }
        }

        val sidebarState = remember { SidebarState() }

        Row {
            Column(Modifier.fillMaxWidth(0.15f)) {
                SpellListSaver(filter, savedFilters, savedNames, saveFilter)
                FilterDivider()
                SpellFilterEditor(filter) {
                    filter = it
                }
            }

            sidebarState.withNew {
                if (spells != null) {
                    Box(Modifier.fillMaxHeight().fillMaxWidth().weight(0.8f)) {
                        Box(Modifier.padding(top = 10.dp, start = 10.dp, end = 20.dp)) {
                            LazyColumn(state = scrollState) {
                                items(spells!!.toList(), { it.name }) {
                                    Box(Modifier.padding(bottom = 10.dp)) {
                                        SpellDisplay(it)
                                    }
                                }
                            }
                        }
                        val scrollStyle = ScrollbarStyleAmbient.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
                        VerticalScrollbar(rememberScrollbarAdapter(scrollState, spells!!.size, 100.dp),
                            Modifier.align(Alignment.CenterEnd),
                            scrollStyle)
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