package com.rnett.spellbook.ui.pages.spellbooks.spellcasting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rnett.spellbook.ui.components.spellbook.Spellcasting
import com.rnett.spellbook.ui.pages.spellbooks.EditScreen
import kotlinx.collections.immutable.toPersistentList

class EditSpellcastingsScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = EditScreen.editScreenModel()
        val spellbook = screenModel.loadedSpellbook.spellbook
        val navigator = LocalNavigator.currentOrThrow

        LazyRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            itemsIndexed(spellbook.spellcastings, { _, it -> it.name }) { idx, it ->
                Spellcasting(it) {
                    screenModel.update(
                        spellbook.copy(spellcastings = spellbook.spellcastings.toPersistentList().set(idx, it))
                    )
                }
                VerticalDivider()
            }

            item {
                TextButton({ navigator.push(AddSpellcastingScreen()) }) {
                    Icon(Icons.Default.Add, "Add spellcasting")
                    Spacer(Modifier.width(5.dp))
                    Text("Add spellcasting", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

    }
}