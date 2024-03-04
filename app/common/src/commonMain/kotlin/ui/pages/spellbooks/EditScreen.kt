package com.rnett.spellbook.ui.pages.spellbooks

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.rnett.spellbook.ui.spellbook.LocalSpellbook


class EditScreen() : Screen {
    @Composable
    override fun Content() {
        val loadedSpellbook = LocalSpellbook.current.loadedSpellbook
        if (loadedSpellbook == null) {
            LocalNavigator.current?.push(NewScreen())
            return
        }

        Text("Edit ${loadedSpellbook.spellbook.name} [${loadedSpellbook.dao.name}]")
    }
}