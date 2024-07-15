package com.rnett.spellbook.ui.pages.spellbooks

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rnett.spellbook.data.SpellbookReference
import com.rnett.spellbook.model.spellbook.Spellbook


@Composable
fun EditSpellbook(reference: SpellbookReference) {
    var loadedSpellbook by remember { mutableStateOf<Spellbook?>(null) }

    LaunchedEffect(reference) {
        val loaded = reference.load()
        if (loaded != null) {
            loadedSpellbook = loaded
        }
    }

    if (loadedSpellbook == null) {
        CircularProgressIndicator()
    }

    loadedSpellbook?.let { spellbook ->
        val screenModel = remember {
            SpellbookEditScreenModel(
                reference,
                spellbook
            )
        }

        Column(Modifier.padding(20.dp)) {
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            Navigator(EditSpellcastingsScreen()) {
                LocalNavigator.currentOrThrow.rememberNavigatorScreenModel { screenModel }
                CurrentScreen()
            }
        }
    }
}