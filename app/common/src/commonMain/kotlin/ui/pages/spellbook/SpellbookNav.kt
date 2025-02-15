package com.rnett.spellbook.ui.pages.spellbook

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable

object SpellbookRoutes {

    @Serializable
    object OpenOrNew

    @Serializable
    data class Edit(val spellbookKey: String)
}


fun NavGraphBuilder.SpellbookNav(navController: NavController) {

    composable<SpellbookRoutes.OpenOrNew> {
        OpenOrNewSpellbookPage() {
            navController.navigate(SpellbookRoutes.Edit(it)) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }
}