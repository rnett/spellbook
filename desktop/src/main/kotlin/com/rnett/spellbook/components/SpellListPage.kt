package com.rnett.spellbook.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollbarStyleAmbient
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.Spell
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.db.getSpellsForFilter
import com.rnett.spellbook.filter.SpellFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

@OptIn(ExperimentalFoundationApi::class, androidx.compose.animation.ExperimentalAnimationApi::class)
@Composable
fun SpellListPage() {
    DesktopMaterialTheme() {
        Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
            var filter by remember { mutableStateOf(SpellFilter(), referentialEqualityPolicy()) }

            var spells by remember { mutableStateOf<Set<Spell>?>(null) }
            val scrollState = rememberLazyListState()

            LaunchedEffect(filter) {
                withContext(Dispatchers.IO) {
                    spells = newSuspendedTransaction {
                        getSpellsForFilter(filter)
                    }
                }
//                scrollState.scrollToItem(0)
            }

            val sidebarState = remember { SidebarState() }

            Row {
                Column(Modifier.fillMaxWidth(0.15f)) {
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
                                        BoxWithConstraints {
                                            Box(Modifier.padding(bottom = 10.dp)) {
                                                SpellDisplay(it)
                                            }
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
}