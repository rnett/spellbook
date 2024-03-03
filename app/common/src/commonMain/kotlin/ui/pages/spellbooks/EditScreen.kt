package com.rnett.spellbook.ui.pages.spellbooks

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.rnett.spellbook.data.LoadedSpellbook


class EditScreen(val loadedSpellbook: LoadedSpellbook) : Screen {
    @Composable
    override fun Content() {
        Text("Edit ${loadedSpellbook.spellbook.name} [${loadedSpellbook.dao.name}]")
    }
}