package com.rnett.spellbook.ui.components

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LeadingIconTab
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun NavTab(navController: NavController, route: Any, tabDisplay: @Composable ColumnScope.(Boolean) -> Unit) {
    NavTabSetup(navController, route) { active, onCLick ->
        Tab(active, onCLick, Modifier.fillMaxWidth()) {
            tabDisplay(active)
        }
    }
}

@Composable
fun NavTab(navController: NavController, route: Any, title: String) {
    NavTabSetup(navController, route) { active, onCLick ->
        Tab(
            active,
            onCLick,
            Modifier.fillMaxWidth(),
            text = { Text(title) }
        )
    }
}

@Composable
fun NavTab(navController: NavController, route: Any, title: String, icon: ImageVector) {
    NavTabSetup(navController, route) { active, onCLick ->
        LeadingIconTab(
            active,
            onCLick,
            text = { Text(title) },
            icon = { Icon(icon, title) },

            Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun NavTabSetup(
    navController: NavController,
    route: Any,
    tabDisplay: @Composable (Boolean, () -> Unit) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val active = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
    Surface(Modifier.width(200.dp), tonalElevation = if (active) 20.dp else 0.dp) {
        tabDisplay(active) {
            navController.navigate(route) {
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
    }
}