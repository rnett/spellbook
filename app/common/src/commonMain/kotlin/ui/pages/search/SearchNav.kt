package com.rnett.spellbook.ui.pages.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

data object SearchRoutes {
    @Serializable
    data class Search(val name: String)
}

fun NavGraphBuilder.SearchNav(navController: NavController) {
    composable<SearchRoutes.Search> { SearchPage(it.toRoute<SearchRoutes.Search>().name) }
}