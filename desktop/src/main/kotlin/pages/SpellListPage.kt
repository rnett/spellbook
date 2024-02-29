package com.rnett.spellbook.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.icons.Icons
import androidx.compose.material3.icons.filled.UnfoldLess
import androidx.compose.material3.icons.filled.UnfoldMore
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.components.IconButtonHand
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.filter.FilterDivider
import com.rnett.spellbook.components.filter.SpellFilterEditor
import com.rnett.spellbook.components.search.SpellFilterLoader
import com.rnett.spellbook.components.search.SpellFilterSaver
import com.rnett.spellbook.components.spell.SpellDisplay
import com.rnett.spellbook.db.getSpellsForFilter
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.ifLet
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spellbook.SpellSlotSpec
import com.rnett.spellbook.spellbook.forSlot
import com.rnett.spellbook.spellbook.withoutAnySlot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext


sealed class SpellListState {
    abstract var filter: SpellFilter

    abstract val level: SpellSlotSpec?

    class Search(filter: SpellFilter) :
        SpellListState() {
        override var filter: SpellFilter by mutableStateOf(filter)
        override val level: SpellSlotSpec? = null
    }

    class FindForSpellbook(
        filter: SpellFilter,
        override val level: SpellSlotSpec,
        val setSpell: (Spell) -> Unit
    ) : SpellListState() {
        override var filter: SpellFilter by mutableStateOf(filter.forSlot(level))
    }

}

//TODO option to show hightenable spells at their hightened levels like AoN

//TODO I'd like to be able to tab from search box to first spell, then select it or add to cart.
//  I want a way to do the filters w/ the keyboard, too

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun SpellListPage(
    state: SpellListState
) {
    Surface(
        color = MainColors.outsideColor.asCompose(),
        contentColor = MainColors.textColor.asCompose(),
    ) {
        var loadingSavedFilter by remember { mutableStateOf(false) }

        var spells by remember { mutableStateOf<List<Spell>?>(null) }
        val scrollState = rememberLazyListState()

        LaunchedEffect(state.filter) {
            withContext(Dispatchers.IO) {
                spells = getSpellsForFilter(state.filter).let {
                    if (state is SpellListState.Search && state.level != null)
                        it.filter { state.level!!.accepts(it) }
                    else
                        it
                }
            }
        }

        key(state.filter) {
            if (state.filter == SpellFilter().forSlot(state.level)) {
                LaunchedEffect(state.filter) {
                    scrollState.animateScrollToItem(0)
                }
            }
        }

        val globalExpanded = remember {
            MutableSharedFlow<Boolean>(
                extraBufferCapacity = 3,
                onBufferOverflow = BufferOverflow.DROP_OLDEST
            )
        }

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
            SpellList(scrollState, spells, state, globalExpanded)
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