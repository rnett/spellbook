package com.rnett.spellbook.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.SidebarDisplay
import com.rnett.spellbook.components.SidebarState
import com.rnett.spellbook.components.filter.FilterDivider
import com.rnett.spellbook.components.filter.SpellFilterEditor
import com.rnett.spellbook.components.search.SpellFilterLoader
import com.rnett.spellbook.components.search.SpellFilterSaver
import com.rnett.spellbook.components.spell.SpellDisplay
import com.rnett.spellbook.db.getSpellsForFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.LevelKnownSpell
import com.rnett.spellbook.spellbook.forSlot
import com.rnett.spellbook.spellbook.withoutAnySlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext


sealed class SpellListState {
    abstract var filter: SpellFilter

    abstract val level: LevelKnownSpell?

    class Search(filter: SpellFilter) :
        SpellListState() {
        override var filter: SpellFilter by mutableStateOf(filter)
        override val level: LevelKnownSpell? = null
    }

    class FindForSpellbook(
        filter: SpellFilter,
        override val level: LevelKnownSpell,
        val setSpell: (Spell) -> Unit
    ) : SpellListState() {
        override var filter: SpellFilter by mutableStateOf(filter.forSlot(level))
    }

}

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun SpellListPage(
    state: SpellListState
) {
    Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
        var loadingSavedFilter by remember { mutableStateOf(false) }

        var spells by remember { mutableStateOf<List<Spell>?>(null) }
        val scrollState = rememberLazyListState()

        LaunchedEffect(state.filter) {
            withContext(Dispatchers.IO) {
                spells = getSpellsForFilter(state.filter)
            }
        }

        key(state.filter) {
            if (state.filter == SpellFilter().forSlot(state.level)) {
                LaunchedEffect(state.filter) {
                    scrollState.animateScrollToItem(0)
                }
            }
        }

        val globalExpanded = MutableSharedFlow<Boolean>(extraBufferCapacity = 3)

        Row {
            Column(Modifier.fillMaxWidth(0.15f)) {
                if (state is SpellListState.FindForSpellbook) {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text("Select spell...", fontWeight = FontWeight.Bold)
                    }
                }
                if (loadingSavedFilter) {
                    SpellFilterLoader({ loadingSavedFilter = false }) {
                        state.filter = it.forSlot(state.level)
                        loadingSavedFilter = false
                    }
                } else {
                    SpellFilterSaver(
                        state.filter,
                        { filter ->
                            filter.ifLet(state.level != null) { it.withoutAnySlot() }
                        },
                        { loadingSavedFilter = true }
                    ) {
                        Spacer(Modifier.width(5.dp))
                        IconButtonHand({
                            globalExpanded.tryEmit(true)
                        }, Modifier.width(24.dp)) {
                            IconWithTooltip(Icons.Default.UnfoldMore, "Expand All")
                        }
                        IconButtonHand({
                            globalExpanded.tryEmit(false)
                        }, Modifier.width(24.dp)) {
                            IconWithTooltip(Icons.Default.UnfoldLess, "Collapse All")
                        }
                    }
                    FilterDivider()
                    SpellFilterEditor(state.filter, state.level) {
                        state.filter = it.forSlot(state.level)
                    }
                }
            }

            if (state is SpellListState.Search) {

                val sidebarState = remember { SidebarState() }

                sidebarState.withNew {
                    SpellList(scrollState, spells, state, globalExpanded)
                }

                AnimatedVisibility(sidebarState.active, Modifier.fillMaxWidth().weight(0.2f)) {
                    if (sidebarState.current != null) {
                        SidebarDisplay(sidebarState.current!!, sidebarState)
                    }
                }
            } else {
                SpellList(scrollState, spells, state, globalExpanded)
            }
        }
    }
}

@Composable
private fun RowScope.SpellList(
    scrollState: LazyListState,
    spells: List<Spell>?,
    state: SpellListState,
    globalExpanded: MutableSharedFlow<Boolean>
) {
    if (spells != null) {
        Box(Modifier.fillMaxHeight().fillMaxWidth().weight(0.8f)) {
            Box(Modifier.padding(top = 10.dp, start = 10.dp, end = 20.dp)) {
                LazyColumn(state = scrollState) {
                    items(spells.toList(), { it.name }) {
                        Box(Modifier.padding(bottom = 10.dp)) {
                            SpellDisplay(
                                it,
                                if (state is SpellListState.FindForSpellbook) state.setSpell else null,
                                globalExpanded
                            )
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