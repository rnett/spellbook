package com.rnett.spellbook

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.rnett.spellbook.components.spell.SpellListTag
import com.rnett.spellbook.data.allDurations
import com.rnett.spellbook.data.allSpellConditions
import com.rnett.spellbook.data.allSpellTraits
import com.rnett.spellbook.data.allSpells
import com.rnett.spellbook.data.allTargeting
import com.rnett.spellbook.data.allTraits
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.pages.SpellListPage
import com.rnett.spellbook.pages.SpellbooksPage
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.LevelKnownSpell
import com.rnett.spellbook.spellbook.SpellbookType
import com.rnett.spellbook.spellbook.Spellcasting
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

enum class Pages {
    Spellbooks, SpellSearch;
}

fun initCaches() {
    allSpells.let { }
    allTraits.let { }
    allSpellTraits.let { }
    allSpellConditions.let { }
    allTargeting.let { }
    allDurations.let { }
}

class MainState(
    val savedSearchRepo: LocalNamedObjectRepo<SpellFilter>, //TODO just use mutable state + LaunchedEffect keyed on it to save?
    initialPage: Pages
) {
    val savedSearches = savedSearchRepo.state
    var lookingForSpell by mutableStateOf<LevelKnownSpell?>(null)

    val searchPage = PageState.Search()
    val spellbookPage = PageState.Spellbooks()

    private val derivedHelper by derivedStateOf { page.page }

    var currentPage
        get() = derivedHelper
        set(p) {
            page = when (p) {
                Pages.Spellbooks -> spellbookPage
                Pages.SpellSearch -> searchPage
            }
        }

    var page by mutableStateOf<PageState>(
        when (initialPage) {
            Pages.Spellbooks -> spellbookPage
            Pages.SpellSearch -> searchPage
        }
    )
        private set

}

//TODO finish
sealed class PageState {
    abstract val page: Pages

    @Composable
    abstract fun show(main: MainState)

    class Search : PageState() {
        override val page: Pages = Pages.SpellSearch

        var search: SpellFilter by mutableStateOf(SpellFilter())

        @Composable
        override fun show(main: MainState) = with(main) {
            val savedSearchesState by savedSearches.collectAsState()
            SpellListPage(
                search,
                { search = it },
                savedSearchesState,
                { savedSearchRepo.value = it },
                { name, value -> savedSearchRepo.value = savedSearchesState.set(name, value) },
                null,
                null
            )
        }
    }

    class Spellbooks : PageState() {
        override val page: Pages = Pages.Spellbooks

        @Composable
        override fun show(main: MainState) = with(main) {
            val spells = mutableStateListOf(
                "Main" to Spellcasting.fullCaster(
                    SpellbookType.Spontaneous,
                    setOf(SpellList.Arcane),
                    4
                )
            )
            SpellbooksPage(
                spells,
                { idx, new ->
                    spells[idx] = spells[idx].first to new
                }) { slot, setter ->
            }
        }
    }
}

//TODO need a cohesive global state
sealed class NextSearch {
    data class Filter(val filter: SpellFilter) : NextSearch()
    data class Slot(val slot: LevelKnownSpell, val set: (Spell) -> Unit) : NextSearch()

    inline val filterOrNull get() = if (this is Filter) filter else null
    inline val slotOrNull get() = if (this is Slot) slot else null
    inline val slotSetterOrNull get() = if (this is Slot) set else null
}

fun main() {
//    SpellbookDB.initH2()

    GlobalScope.launch {
        initCaches()
    }

    singleWindowApplication(WindowState(placement = WindowPlacement.Maximized)) {
        val focusManager = LocalFocusManager.current
        Surface(
            Modifier.fillMaxSize().clickable(remember { MutableInteractionSource() }, null) {
                focusManager.clearFocus()
                //TODO this is a bit of a hack.  I want to lose focus whenever I click outside of the focused element
            },
            color = MainColors.outsideColor.asCompose(),
            contentColor = MainColors.textColor.asCompose()
        ) {
            DesktopMaterialTheme() {
                Column(Modifier.fillMaxSize()) {

                    val savedSearchRepo = remember { LocalSavedSearchRepo({}) }

                    val mainState = remember { MainState(savedSearchRepo, Pages.Spellbooks) }

                    mainState.lookingForSpell?.let {
                        Row {
                            Text("Selecting level ${it.level} ${it.slot.type.longName} from {")
                            Spacer(Modifier.width(0.5.dp))
                            it.slot.lists.forEach {
                                SpellListTag(it)
                                Spacer(Modifier.width(0.5.dp))
                            }
                            Text("}")
                        }
                    }

                    TabRow(mainState.currentPage.ordinal, backgroundColor = MainColors.spellBodyColor.asCompose()) {
                        Tab(mainState.currentPage == Pages.Spellbooks, {
                            mainState.currentPage = Pages.Spellbooks
                        }) {
                            Text("Spellbooks", Modifier.padding(10.dp))
                        }
                        Tab(mainState.currentPage == Pages.SpellSearch, {
                            mainState.currentPage = Pages.SpellSearch
                        }) {
                            Text("Search", Modifier.padding(10.dp))
                        }
                    }

                    mainState.page.show(mainState)
                }
            }
        }
    }
}