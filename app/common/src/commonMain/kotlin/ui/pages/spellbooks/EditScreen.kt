package com.rnett.spellbook.ui.pages.spellbooks

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.rememberNavigatorScreenModel
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.rnett.spellbook.data.LoadedSpellbook
import com.rnett.spellbook.model.spellbook.Spellbook
import com.rnett.spellbook.ui.pages.spellbooks.spellcasting.EditSpellcastingsScreen
import com.rnett.spellbook.ui.spellbook.LocalSpellbook
import com.rnett.spellbook.ui.spellbook.LocalSpellbookState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch

data class SpellbookEditScreenModel(
    val loadedSpellbook: LoadedSpellbook,
    val spellbookNavigator: Navigator,
    val localSpellbookState: LocalSpellbookState
) : ScreenModel {
    fun closeSpellbook() {
        localSpellbookState.loadedSpellbook = null
        spellbookNavigator.popUntil<NewScreen, _>()
    }

    private val savedSpellbook = MutableStateFlow(loadedSpellbook.spellbook)

    suspend fun saveUpdates() {
        savedSpellbook.runningReduce { old, new ->
            loadedSpellbook.dao.saveSpellbook(old.name, new)
            return@runningReduce new
        }
    }

    fun update(spellbook: Spellbook) {
        savedSpellbook.value = spellbook
        screenModelScope.launch {
            localSpellbookState.loadedSpellbook = LoadedSpellbook(loadedSpellbook.dao, spellbook.name, spellbook)
        }
    }

    fun update(spellbook: (Spellbook) -> Spellbook) {
        val newValue = savedSpellbook.updateAndGet(spellbook)
        screenModelScope.launch {
            localSpellbookState.loadedSpellbook = LoadedSpellbook(loadedSpellbook.dao, newValue.name, newValue)
        }
    }
}

class EditScreen() : Screen {
    @Composable
    override fun Content() {
        val loadedSpellbook = LocalSpellbook.current.loadedSpellbook
        if (loadedSpellbook == null) {
            LocalNavigator.current?.push(NewScreen())
            return
        }

        val spellbookNavigator = LocalNavigator.currentOrThrow
        val localSpellbookState = LocalSpellbook.current

        val screenModel = rememberScreenModel {
            SpellbookEditScreenModel(
                loadedSpellbook,
                spellbookNavigator,
                localSpellbookState
            )
        }

        Column(Modifier.padding(20.dp)) {

            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(loadedSpellbook.spellbook.name, style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.weight(1f))
                IconButton({
                    screenModel.closeSpellbook()
                }) {
                    Icon(Icons.Default.Close, "Close")
                }
            }

            Spacer(Modifier.height(5.dp))
            HorizontalDivider()
            Spacer(Modifier.height(20.dp))

            Navigator(EditSpellcastingsScreen()) {
                LocalNavigator.currentOrThrow.rememberNavigatorScreenModel { screenModel }
                CurrentScreen()
            }
        }
    }

    companion object {
        @Composable
        fun editScreenModel() =
            LocalNavigator.currentOrThrow.rememberNavigatorScreenModel<SpellbookEditScreenModel> { error("Must already be initialized") }
    }
}