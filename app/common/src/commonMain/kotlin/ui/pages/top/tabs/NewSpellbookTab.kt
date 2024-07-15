package com.rnett.spellbook.ui.pages.top.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.rnett.spellbook.ui.MainPageModel
import com.rnett.spellbook.ui.components.spellbook.SpellbookLoaderAndCreator
import com.rnett.spellbook.ui.support.IconTab

object NewSpellbookTab : IconTab {
    @Composable
    override fun Content() {
        val model =
            LocalNavigator.currentOrThrow.rememberNavigatorScreenModel<MainPageModel> { error("Must already be present") }
        SpellbookLoaderAndCreator(model::openOrSelectSpellbook, model::openOrSelectSpellbook)
    }

    override val options: TabOptions
        @Composable get() = TabOptions(
            0u,
            "New / Load",
            rememberVectorPainter(Icons.Default.Add)
        )
}