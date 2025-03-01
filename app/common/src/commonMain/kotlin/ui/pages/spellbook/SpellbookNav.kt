package com.rnett.spellbook.ui.pages.spellbook

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rnett.spellbook.data.SpellbookDaoKey
import com.rnett.spellbook.data.SpellbookReference
import kotlinx.serialization.Serializable

data object SpellbookRoutes {

    @Serializable
    data object OpenOrNew

    @Serializable
    data class Edit(val daoKey: String, val spellbookName: String) {
        constructor(reference: SpellbookReference) : this(reference.daoKey.key, reference.name)

        val reference get() = SpellbookReference(SpellbookDaoKey(daoKey), spellbookName)
    }
}


fun NavGraphBuilder.SpellbookNav(navController: NavController) {
    composable<SpellbookRoutes.OpenOrNew> {
        OpenOrNewSpellbookPage {
            navController.navigate(SpellbookRoutes.Edit(it))
        }
    }
    composable<SpellbookRoutes.Edit> {
        SpellbookEditPage(it.toRoute<SpellbookRoutes.Edit>().reference) {
            navController.popBackStack<SpellbookRoutes.OpenOrNew>(inclusive = false)
        }
    }
}