package com.rnett.spellbook

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.desktop.DesktopMaterialTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeviceHub
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.DeviceHub
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerIcon
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.rnett.spellbook.components.DragSetState
import com.rnett.spellbook.components.InfoSidebarState
import com.rnett.spellbook.components.core.ScaleDensityToHeight
import com.rnett.spellbook.components.spell.DraggingSpell
import com.rnett.spellbook.data.allDurations
import com.rnett.spellbook.data.allSpellConditions
import com.rnett.spellbook.data.allSpellTraits
import com.rnett.spellbook.data.allSpells
import com.rnett.spellbook.data.allTargeting
import com.rnett.spellbook.data.allTraits
import com.rnett.spellbook.filter.SpellFilter
import com.rnett.spellbook.group.SpellGroup
import com.rnett.spellbook.pages.CloseSidebarButton
import com.rnett.spellbook.pages.Sidebar
import com.rnett.spellbook.pages.SidebarPage
import com.rnett.spellbook.pages.SidebarState
import com.rnett.spellbook.pages.SidebarToggle
import com.rnett.spellbook.pages.SpellListPage
import com.rnett.spellbook.pages.SpellListState
import com.rnett.spellbook.pages.SpellbooksPage
import com.rnett.spellbook.spell.Spell
import com.rnett.spellbook.spell.SpellList
import com.rnett.spellbook.spellbook.Spellbook
import com.rnett.spellbook.spellbook.Spellcasting
import com.rnett.spellbook.spellbook.SpellcastingType
import com.rnett.spellbook.spellbook.withLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.awt.Cursor
import kotlin.random.Random

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

class ShoppingCart(private val items: MutableList<Spell>) : List<Spell> by items {

    fun add(item: Spell) {
        if (item !in items)
            items += item
    }

    operator fun plusAssign(item: Spell) = add(item)

    fun remove(item: Spell) {
        items.removeIf { item.name == it.name }
    }

    operator fun minusAssign(item: Spell) = remove(item)
}

class MainState(
    val savedSearchRepo: LocalNamedObjectRepo<SpellFilter>, //TODO just use mutable state + LaunchedEffect keyed on it to save?
    coroutineScope: CoroutineScope,
    val globalKeyEvents: MutableSharedFlow<KeyEvent>,
    initialPage: Pages
) {

    val searchPage = PageState.Search()
    val spellbookPage = PageState.Spellbooks()

    var sidebarPage by mutableStateOf<SidebarPage?>(null)

    val shoppingCart = ShoppingCart(mutableStateListOf())
    val infoState = InfoSidebarState({ sidebarPage = SidebarPage.Info }) { sidebarPage = null }

    private val spellRandom = Random(0)

    private fun spells(n: Int) = List(n) { allSpells[spellRandom.nextInt(allSpells.size)] }

    val groups = mutableStateMapOf<String, SpellGroup>(
        "Magus Spells" to SpellGroup(
            spells(10),
            mapOf("Teleport" to SpellGroup(spells(20)), "Melee" to SpellGroup(spells(20)))
        ),
        "Debuf Spells" to SpellGroup(
            spells(10),
            mapOf("Fort" to SpellGroup(spells(20)), "Will" to SpellGroup(spells(20)))
        ),
        "Sorc Spells" to SpellGroup(
            spells(10),
            mapOf("Damage" to SpellGroup(spells(20)), "Util" to SpellGroup(spells(20)))
        ),
        "Illusion Spells" to SpellGroup(
            spells(10),
            mapOf("Main" to SpellGroup(spells(20)), "Off" to SpellGroup(spells(20)))
        ),
    )
    val sidebarState = SidebarState(infoState, shoppingCart, groups) { sidebarPage = null }

    val dragSpellsToSide = DragSetState<Spell>(coroutineScope)
    val dragSpellsFromSide = DragSetState<Spell>(coroutineScope)

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
                                    .withKnown(1, allSpells.first { it.name == "Animate Dead" })
                            })
                        },
                        "Wizard Archetype" to Spellcasting.archetypeCaster(
                            SpellcastingType.Prepared,
                            setOf(SpellList.Arcane)
                        ).let {
                            it.withLevel(3, it[3].let {
                                it.withKnown(0, allSpells.first { it.name == "Fireball" })
                                    .withKnown(1, allSpells.first { it.name == "Animate Dead" })
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

@Composable
private fun WithMainState(
    savedSearchRepo: LocalNamedObjectRepo<SpellFilter>,
    globalKeyEvents: MutableSharedFlow<KeyEvent>,
    initialPage: Pages,
    content: @Composable MainState.() -> Unit
) {

    val scope = rememberCoroutineScope { Dispatchers.Default }
    val mainState = remember { MainState(savedSearchRepo, scope, globalKeyEvents, initialPage) }

    CompositionLocalProvider(
        LocalMainState.provides(mainState)
    ) {
        with(mainState) {
            content()
        }
    }
}

//TODO rework focus

@OptIn(
    ExperimentalAnimationApi::class, org.jetbrains.compose.splitpane.ExperimentalSplitPaneApi::class,
    androidx.compose.ui.ExperimentalComposeUiApi::class
)
fun main() {
//    SpellbookDB.initH2()

    GlobalScope.launch {
        initCaches()
    }

    val globalKeyEvents =
        MutableSharedFlow<KeyEvent>(extraBufferCapacity = 2, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    singleWindowApplication(WindowState(placement = WindowPlacement.Maximized), onKeyEvent = {
        globalKeyEvents.tryEmit(it)
        false
    }) {
        val focusManager = LocalFocusManager.current
        Surface(
            Modifier.fillMaxSize()
//                .clickable(remember { MutableInteractionSource() }, null) {
//                    focusManager.clearFocus()
//                    //TODO this is a bit of a hack.  I want to lose focus whenever I click outside of the focused element
//                }
            ,
            color = MainColors.outsideColor.asCompose(),
            contentColor = MainColors.textColor.asCompose()
        ) {
            DesktopMaterialTheme() {
                ScaleDensityToHeight(1300f, 0.6f, scale = 0.6f, offset = 0.3f) {
                    Column(Modifier.fillMaxSize()) {

                        val savedSearchRepo = remember { LocalSavedSearchRepo({}) }

                        WithMainState(savedSearchRepo, globalKeyEvents, Pages.Spellbooks) {
                            Row(Modifier.fillMaxWidth().background(MainColors.spellBodyColor.asCompose())) {

                                TabRow(
                                    currentPage.ordinal,
                                    Modifier.weight(1f),
                                    backgroundColor = MainColors.spellBodyColor.asCompose()
                                ) {
                                    Tab(currentPage == Pages.Spellbooks, {
                                        currentPage = Pages.Spellbooks
                                    }) {
                                        Text("Spellbooks", Modifier.padding(10.dp))
                                    }
                                    Tab(currentPage == Pages.SpellSearch, {
                                        currentPage = Pages.SpellSearch
                                    }) {
                                        Text("Search", Modifier.padding(10.dp))
                                    }
                                }

                                Spacer(Modifier.width(20.dp))

                                SidebarToggle(SidebarPage.Cart, Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart)

                                Spacer(Modifier.width(10.dp))

                                SidebarToggle(SidebarPage.Groups, Icons.Filled.DeviceHub, Icons.Outlined.DeviceHub)

                                Spacer(Modifier.width(10.dp))

                                SidebarToggle(
                                    SidebarPage.Info,
                                    Icons.Filled.Info,
                                    Icons.Outlined.Info,
                                    infoState.hasCurrent
                                )

                                Spacer(Modifier.width(10.dp))

                                CloseSidebarButton()

                                Spacer(Modifier.width(10.dp))

                            }

                            dragSpellsToSide.display {
                                DraggingSpell(it)
                            }

                            dragSpellsFromSide.display {
                                DraggingSpell(it)
                            }

                            var splitPercent by remember { mutableStateOf(0.92f) }
                            var width by remember { mutableStateOf(0f) }
                            val dragState = rememberDraggableState {
                                val newPercent = splitPercent + (it / width)
                                splitPercent = newPercent.coerceIn(0.1f, 0.97f)
                            }

                            Row(Modifier.fillMaxSize().onGloballyPositioned {
                                width = it.boundsInParent().width
                            }) {
                                Row(Modifier.weight(splitPercent)) {
                                    infoState.withNew {
                                        page.show(this@WithMainState)
                                    }
                                }
                                if (sidebarPage != null) {
                                    Box(
                                        Modifier
                                            .width(4.dp)
                                            .fillMaxHeight()
                                            .background(Color.LightGray)
                                            .pointerIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
                                            .draggable(dragState, Orientation.Horizontal)
                                    )
                                    AnimatedVisibility(
                                        sidebarPage != null,
                                        Modifier.fillMaxWidth().weight(1f - splitPercent),
                                        enter = fadeIn() + expandHorizontally(),
                                        exit = shrinkHorizontally() + fadeOut()
                                    ) {

                                        sidebarPage?.let {
                                            Sidebar(sidebarState, it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}