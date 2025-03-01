package com.rnett.spellbook.ui.components.spellbook.edit

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rnett.spellbook.model.spellbook.Spellbook
import kotlinx.collections.immutable.toPersistentList
import kotlinx.serialization.Serializable

data object SpellbookEditorRoutes {
    @Serializable
    data object EditSpellcastings

    @Serializable
    data object AddSpellcasting {
    }
}

@Composable
fun SpellbookEditor(
    spellbook: Spellbook,
    update: (Spellbook) -> Unit,
) {
    val navController = rememberNavController()
    NavHost(navController, SpellbookEditorRoutes.EditSpellcastings) {
        composable<SpellbookEditorRoutes.EditSpellcastings> {
            SpellcastingsEditor(spellbook, update) {
                navController.navigate(SpellbookEditorRoutes.AddSpellcasting)
            }
        }
        composable<SpellbookEditorRoutes.AddSpellcasting> {
            AddSpellcasting(spellbook) {
                update(spellbook.copy(spellcastings = spellbook.spellcastings.toPersistentList().add(it.inflate())))
            }
        }
    }
}
