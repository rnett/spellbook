package com.rnett.spellbook

import androidx.compose.desktop.AppWindow
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.desktop.WindowEvents
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import com.rnett.spellbook.data.allDurations
import com.rnett.spellbook.data.allSpellConditions
import com.rnett.spellbook.data.allSpellTraits
import com.rnett.spellbook.data.allSpells
import com.rnett.spellbook.data.allTargeting
import com.rnett.spellbook.data.allTraits
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.pages.SavedSearchPage
import com.rnett.spellbook.pages.SpellListPage
import com.rnett.spellbook.pages.SpellbooksPage
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.LevelSlot
import com.rnett.spellbook.spellbook.Spellbook
import com.rnett.spellbook.spellbook.SpellbookType
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities

fun MaximizeWindow(
    title: String = "JetpackDesktopWindow",
    size: IntSize = IntSize(1920, 1024),
    location: IntOffset = IntOffset(-1920, 0),
    centered: Boolean = true,
    icon: BufferedImage? = null,
    menuBar: MenuBar? = null,
    undecorated: Boolean = false,
    resizable: Boolean = true,
    events: WindowEvents = WindowEvents(),
    onDismissRequest: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) = SwingUtilities.invokeLater {
    AppWindow(
        title = title,
        size = size,
        location = location,
        centered = centered,
        icon = icon,
        menuBar = menuBar,
        undecorated = undecorated,
        resizable = resizable,
        events = events,
        onDismissRequest = onDismissRequest
    ).also { it.maximize() }.show {
        content()
    }
}

enum class Pages {
    Spellbooks, SpellSearch, SavedSearches;
}

fun initCaches() {
    allSpells.let { }
    allTraits.let { }
    allSpellTraits.let { }
    allSpellConditions.let { }
    allTargeting.let { }
    allDurations.let { }
}

sealed class NextSearch {
    data class Filter(val filter: SpellFilter) : NextSearch()
    data class Slot(val slot: LevelSlot) : NextSearch()

    val filterOrNull get() = if (this is Filter) filter else null
    val slotOrNull get() = if (this is Slot) slot else null
}

fun main() {
//    SpellbookDB.initH2()

    GlobalScope.launch {
        initCaches()
    }

    MaximizeWindow("Spellbook") {
        Surface(Modifier.fillMaxSize(), color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
            DesktopMaterialTheme() {
                Column(Modifier.fillMaxSize()) {
                    var currentPage by remember { mutableStateOf(Pages.Spellbooks) }

                    val nextSearchFlow = remember { MutableStateFlow<NextSearch?>(null) }

                    val nextSearch: NextSearch? by nextSearchFlow.collectAsState()

                    val savedSearchRepo = remember { LocalSavedSearchRepo({}) }

                    TabRow(currentPage.ordinal, backgroundColor = MainColors.spellBodyColor.asCompose()) {
                        Tab(currentPage == Pages.Spellbooks, {
                            nextSearchFlow.value = null
                            currentPage = Pages.Spellbooks
                        }) {
                            Text("Spellbooks", Modifier.padding(10.dp))
                        }
                        Tab(currentPage == Pages.SpellSearch, {
                            nextSearchFlow.value = null
                            currentPage = Pages.SpellSearch
                        }) {
                            Text("Search", Modifier.padding(10.dp))
                        }
                        Tab(currentPage == Pages.SavedSearches, {
                            nextSearchFlow.value = null
                            currentPage = Pages.SavedSearches
                        }) {
                            Text("Saved Searches", Modifier.padding(10.dp))
                        }
                    }

                    when (currentPage) {
                        Pages.Spellbooks -> {
                            SpellbooksPage(listOf("Main" to Spellbook.fullCaster(SpellbookType.Spontaneous, SpellList.Arcane, 4))) {
                                nextSearchFlow.value = NextSearch.Slot(it)
                                currentPage = Pages.SpellSearch
                            }
                        }
                        Pages.SpellSearch -> {
                            SpellListPage(nextSearch?.filterOrNull ?: SpellFilter(),
                                savedSearchRepo.all,
                                savedSearchRepo::add,
                                nextSearch?.slotOrNull
                            )
                        }
                        Pages.SavedSearches -> SavedSearchPage(savedSearchRepo.all,
                            savedSearchRepo::remove,
                            savedSearchRepo::set,
                            savedSearchRepo::rename,
                            savedSearchRepo::add) {
                            nextSearchFlow.value = NextSearch.Filter(savedSearchRepo[it].second)
                            currentPage = Pages.SpellSearch
                        }
                    }
                }
            }
        }
    }
}