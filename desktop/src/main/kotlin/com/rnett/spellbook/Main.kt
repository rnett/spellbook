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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import com.rnett.spellbook.components.SavedSearchPage
import com.rnett.spellbook.components.SpellListPage
import com.rnett.spellbook.filter.SpellFilter
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
    SavedSearches, SpellSearch;
}

fun main() {
//    SpellbookDB.initH2()
    MaximizeWindow("Spellbook") {
        Surface(Modifier.fillMaxSize(), color = MainColors.outsideColor.asCompose(), contentColor = MainColors.textColor.asCompose()) {
            DesktopMaterialTheme() {
                Column(Modifier.fillMaxSize()) {
                    var currentPage by remember { mutableStateOf(Pages.SavedSearches) }
                    var nextSearch: SpellFilter? by remember { mutableStateOf(null) }

                    val savedSearchRepo = remember { LocalSavedSearchRepo({}) }

                    TabRow(currentPage.ordinal, backgroundColor = MainColors.spellBodyColor.asCompose()) {
                        Tab(currentPage == Pages.SavedSearches, {
                            currentPage = Pages.SavedSearches
                        }) {
                            Text("Saved Searches", Modifier.padding(10.dp))
                        }
                        Tab(currentPage == Pages.SpellSearch, {
                            currentPage = Pages.SpellSearch
                        }) {
                            Text("Search", Modifier.padding(10.dp))
                        }
                    }

                    when (currentPage) {
                        Pages.SpellSearch -> {
                            val s = nextSearch
                            nextSearch = null
                            SpellListPage(s ?: SpellFilter(),
                                savedSearchRepo.all.reversed().associate { it.second to it.first },
                                savedSearchRepo.all.map { it.first }.toSet(),
                                savedSearchRepo::add
                            )
                        }
                        Pages.SavedSearches -> SavedSearchPage(savedSearchRepo.all,
                            savedSearchRepo::remove,
                            savedSearchRepo::set,
                            savedSearchRepo::rename,
                            savedSearchRepo::add) {
                            nextSearch = savedSearchRepo[it].second
                            currentPage = Pages.SpellSearch
                        }
                    }
                }
            }
        }
    }
}