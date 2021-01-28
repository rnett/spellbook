package com.rnett.spellbook.components

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.ScrollbarStyleAmbient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Providers
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rnett.spellbook.MainColors
import com.rnett.spellbook.Spell
import com.rnett.spellbook.SpellType
import com.rnett.spellbook.VerticalScrollbar
import com.rnett.spellbook.asCompose
import com.rnett.spellbook.db.getSpellsForFilter
import com.rnett.spellbook.filter.Filter
import com.rnett.spellbook.filter.LevelFilter
import com.rnett.spellbook.filter.SpellFilter
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


@Composable
fun SpellListPage() {
    DesktopMaterialTheme() {
        Surface(color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
            val filter = remember {
                SpellFilter(
                    level = LevelFilter(3),
//                        lists = Filter.Or(SpellList.Focus, SpellList.Arcane),
                    types = Filter.Or(
                        SpellType.Spell, SpellType.Focus
                    )
                )
            }

            var spells by remember { mutableStateOf<Set<Spell>?>(null) }

            LaunchedEffect(filter) {
                spells = newSuspendedTransaction {
                    getSpellsForFilter(filter)
                }
            }

            val sidebarState = remember { SidebarState() }

            Row {
                sidebarState.withNew {
                    if (spells != null) {
                        val scrollState = rememberScrollState()
                        Box(Modifier.fillMaxHeight().fillMaxWidth(if (sidebarState.active) 0.7f else 1f)) {
                            Box(Modifier.padding(top = 10.dp, start = 10.dp, end = 20.dp)) {
                                ScrollableColumn(scrollState = scrollState) {
                                    spells!!.forEach {
                                        Box(Modifier.padding(bottom = 10.dp)) {
                                            SpellDisplay(it)
                                        }
                                    }
                                }
                            }
                            val scrollStyle = ScrollbarStyleAmbient.current.let { it.copy(unhoverColor = it.hoverColor, thickness = 12.dp) }
                            Providers(ScrollbarStyleAmbient provides scrollStyle) {
                                VerticalScrollbar(scrollState, Modifier.align(Alignment.CenterEnd))
                            }
                        }
                    }
                }

                if (sidebarState.active) {
                    Box(Modifier.fillMaxSize()) {
                        SidebarDisplay(sidebarState.current!!, sidebarState)
                    }
                }

            }
        }
    }
}