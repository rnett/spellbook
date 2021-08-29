package com.rnett.spellbook

import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconToggleButton
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.rnett.spellbook.components.IconWithTooltip
import com.rnett.spellbook.components.core.ScaleDensityToHeight
import com.rnett.spellbook.components.handPointer
import com.rnett.spellbook.data.allDurations
import com.rnett.spellbook.data.allSpellConditions
import com.rnett.spellbook.data.allSpellTraits
import com.rnett.spellbook.data.allSpells
import com.rnett.spellbook.data.allTargeting
import com.rnett.spellbook.data.allTraits
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.pages.SpellListPage
import com.rnett.spellbook.pages.SpellListState
import com.rnett.spellbook.pages.SpellbooksPage
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.Spellbook
import com.rnett.spellbook.spellbook.Spellcasting
import com.rnett.spellbook.spellbook.SpellcastingType
import com.rnett.spellbook.spellbook.withLevel
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

val LocalMainState = staticCompositionLocalOf<MainState> { error("MainState not set") }

class MainState(
    val savedSearchRepo: LocalNamedObjectRepo<SpellFilter>, //TODO just use mutable state + LaunchedEffect keyed on it to save?
    initialPage: Pages
) {

    val searchPage = PageState.Search()
    val spellbookPage = PageState.Spellbooks()

    var cartOpen by mutableStateOf(false)

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


    fun saveFilter(name: String, spellFilter: SpellFilter) {
        savedSearchRepo.value = savedSearchRepo.value.replace(name, spellFilter)
    }

    fun updateSavedFilters(searches: SavedSearchs) {
        savedSearchRepo.value = searches
    }

    @Composable
    fun savedFilters() = savedSearchRepo.state.collectAsState()

}

sealed class PageState {
    abstract val page: Pages

    @Composable
    abstract fun show(main: MainState)

    class Search : PageState() {
        override val page: Pages = Pages.SpellSearch

        @Composable
        override fun show(main: MainState) = with(main) {
            val state: SpellListState = remember { SpellListState.Search(SpellFilter()) }
            SpellListPage(
                state
            )
        }
    }

    class Spellbooks : PageState() {
        override val page: Pages = Pages.Spellbooks

        @Composable
        override fun show(main: MainState) = with(main) {
            val spells = mutableStateListOf(
                "Main" to Spellbook(
                    mapOf(
                        "Sorcerer" to Spellcasting.fullCaster(
                            SpellcastingType.Spontaneous,
                            setOf(SpellList.Arcane),
                            4
                        ).let {
                            it.withLevel(3, it[3].let {
                                it.withKnown(0, allSpells.first { it.name == "Fireball" })
                            })
                        },
                        "Wizard Archetype" to Spellcasting.archetypeCaster(
                            SpellcastingType.Prepared,
                            setOf(SpellList.Arcane)
                        ).let {
                            it.withLevel(3, it[3].let {
                                it.withKnown(0, allSpells.first { it.name == "Fireball" })
                            })
                        },
                        "Bard Archetype" to Spellcasting.archetypeCaster(
                            SpellcastingType.Prepared,
                            setOf(SpellList.Occult)
                        ),
                        "Oracle Archetype" to Spellcasting.archetypeCaster(
                            SpellcastingType.Prepared,
                            setOf(SpellList.Divine)
                        )
                    )
                )
            )
            SpellbooksPage(
                spells
            ) { idx, new ->
                spells[idx] = spells[idx].first to new
            }
        }
    }
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
                ScaleDensityToHeight(1300f, 0.6f, scale = 0.6f, offset = 0.3f) {
                    Column(Modifier.fillMaxSize()) {

                        val savedSearchRepo = remember { LocalSavedSearchRepo({}) }

                        val mainState = remember { MainState(savedSearchRepo, Pages.Spellbooks) }

                        CompositionLocalProvider(
                            LocalMainState.provides(mainState)
                        ) {

                            Row(Modifier.fillMaxWidth().background(MainColors.spellBodyColor.asCompose())) {

                                TabRow(
                                    mainState.currentPage.ordinal,
                                    Modifier.weight(1f),
                                    backgroundColor = MainColors.spellBodyColor.asCompose()
                                ) {
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

                                Spacer(Modifier.width(20.dp))

                                IconToggleButton(
                                    mainState.cartOpen,
                                    {
                                        mainState.cartOpen = !mainState.cartOpen
                                    },
                                    Modifier.padding(top = 3.dp).handPointer()
                                        .ifLet(mainState.cartOpen) {
                                            it.background(
                                                Color.White.copy(alpha = 0.3f),
                                                RoundedCornerShape(40, 40, 0, 0)
                                            )
                                        }
                                ) {
                                    IconWithTooltip(
                                        if (mainState.cartOpen) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                                        "Cart"
                                    )
                                }
                                //TODO some kind of button like this for the info pane?  probably not
                                //TODO display cart (do I want groups in the same display? probably, but also separately)

                                Spacer(Modifier.width(20.dp))

                            }

                            mainState.page.show(mainState)
                        }
                    }
                }
            }
        }
    }
}